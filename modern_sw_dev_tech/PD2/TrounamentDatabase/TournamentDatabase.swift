//
//  Tournament.swift
//  PD2
//
//  Created by Gusts Kaksis on 17/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Foundation
import SQLite

class TournamentDatabase
{
    var db:Connection
    var team_id:Int64 = 0

    init(path:String) throws
    {
        db = try Connection(path)

        try TeamTable.createDb(db)
        try PlayerTable.createDb(db)
        try GameTable.createDb(db)
        try GameStatsTable.createDb(db)
        try GamePlayerTable.createDb(db)
        try GameRefereeTable.createDb(db)
        try GameLogTable.createDb(db)
    }
    
    // I'ma just put this here and access from anywhere
    func selectTeamId(_ id:Int64)
    {
        team_id = id
    }

    func addGame(_ game:Game) throws
    {
        for team in game.teams
        {
            TeamTable.updateDb(db: db, team: team)
        }
        
        GameTable.updateDb(db: db, game: game)
        GameStatsTable.updateDb(db: db, stats: game.game_stats[0])
        GameStatsTable.updateDb(db: db, stats: game.game_stats[1])
        GameLogTable.updateDb(db: db, log: game.game_log)
    }
    
    func getTeams() -> [Int64 : String]
    {
        return TeamTable.getList(db)
    }
    
    func getLeagueStatistics() -> [LeagueStatsRow]
    {
        var data = [LeagueStatsRow]()
        do
        {
            let stmt = try db.prepare("""
                SELECT t.name AS team,
                    SUM(s.points) AS points,
                    SUM(s.winner) AS wins,
                    SUM(s.looser) AS losses,
                    SUM(s.winner_ex) AS wins_ex,
                    SUM(s.looser_ex) AS losses_ex,
                    SUM(s.goals) AS goals,
                    SUM(s.against) AS against
                FROM game_stats s
                    JOIN teams t ON (t.id=s.team_id)
                GROUP by s.team_id
                ORDER BY points DESC
                """)
            for _ in stmt
            {
                let item = LeagueStatsRow()
                item.team = stmt.row[0]
                item.points = stmt.row[1]
                item.wins = stmt.row[2]
                item.losses = stmt.row[3]
                item.wins_ex = stmt.row[4]
                item.losses_ex = stmt.row[5]
                item.goals = stmt.row[6]
                item.against = stmt.row[7]
                data.append(item)
            }
        }
        catch
        {
            print("Failed to gather league data")
        }
        return data
    }
    
    func getTopPlayers() -> [TopPlayerRow]
    {
        var data = [TopPlayerRow]()
        do
        {
            let stmt = try db.prepare("""
                SELECT p.name AS player,
                    t.name AS team,
                    COUNT(gl.game_id) AS goals,
                    COUNT(ps.game_id) AS passes
                FROM players p
                    LEFT JOIN game_log gl ON (gl.player_from=p.id AND gl.type=\(GameLogType.Goal.rawValue))
                    LEFT JOIN game_log ps ON (ps.player_from=p.id AND ps.type=\(GameLogType.Pass.rawValue))
                    JOIN teams t ON (t.id=p.team_id)
                GROUP by player, team
                ORDER BY goals DESC, passes DESC
                LIMIT 10
                """)
            for _ in stmt
            {
                let item = TopPlayerRow()
                item.player = stmt.row[0]
                item.team = stmt.row[1]
                item.goals = stmt.row[2]
                item.passes = stmt.row[3]
                data.append(item)
            }
        }
        catch
        {
            print("Failed to gather top player data")
        }
        return data
    }
    
    func getTopGoalkeepers() -> [TopGoalkeeperRow]
    {
        var data = [TopGoalkeeperRow]()
        do
        {
            // TODO: The query actually does not take into account that goalkeepers can change
            // so we should check at what time the goal was registered and which goalie was
            // in the box at the time
            let stmt = try db.prepare("""
                SELECT p.name AS player,
                    t.name AS team,
                    ROUND(SUM(gs.goals + gs.goals_ex) * 1.0 / COUNT(gp.game_id), 1) AS goals
                FROM game_players gp
                    JOIN players p ON (p.id=gp.player_id AND p.position='V')
                    JOIN teams t ON (t.id=p.team_id)
                    LEFT JOIN game_stats gs ON (gs.game_id=gp.game_id)
                GROUP by player, team
                ORDER BY goals ASC
                LIMIT 5
                """)
            for _ in stmt
            {
                let item = TopGoalkeeperRow()
                item.player = stmt.row[0]
                item.team = stmt.row[1]
                item.goals = stmt.row[2]
                data.append(item)
            }
        }
        catch
        {
            print("Failed to gather top goalkeeper data")
        }
        return data
    }
    
    func getTopRudestPlayers() -> [TopRudeRow]
    {
        var data = [TopRudeRow]()
        do
        {
            let stmt = try db.prepare("""
                SELECT p.name AS player,
                    t.name AS team,
                    SUM(gl.game_id) AS penalties
                FROM players p
                    JOIN teams t ON (t.id=p.team_id)
                    LEFT JOIN game_log gl ON (gl.player_from=p.id AND gl.type=\(GameLogType.Penalty.rawValue))
                GROUP by player, team
                ORDER BY penalties DESC
                LIMIT 10
                """)
            for _ in stmt
            {
                let item = TopRudeRow()
                item.player = stmt.row[0]
                item.team = stmt.row[1]
                item.penalties = stmt.row[2]
                data.append(item)
            }
        }
        catch
        {
            print("Failed to gather top rudest player data")
        }
        return data
    }
    
    func getTopReferees() -> [TopRefereeRow]
    {
        var data = [TopRefereeRow]()
        do
        {
            let stmt = try db.prepare("""
                SELECT gr.name AS referee,
                    ROUND(SUM(gs.penalties + gs.penalties_ex) * 1.0 / COUNT(gr.game_id), 1) AS penalties
                FROM game_referees gr
                    LEFT JOIN game_stats gs ON (gs.game_id=gr.game_id)
                WHERE gr.position='VT'
                GROUP by referee
                ORDER BY penalties DESC
                LIMIT 5
                """)
            for _ in stmt
            {
                let item = TopRefereeRow()
                item.referee = stmt.row[0]
                item.penalties = stmt.row[1]
                data.append(item)
            }
        }
        catch
        {
            print("Failed to gather top goalkeeper data")
        }
        return data
    }
    
    func getTeamPlayers() -> [TeamPlayerRow]
    {
        var data = [TeamPlayerRow]()
        do
        {
            let stmt = try db.prepare("""
                SELECT p.number,
                    p.name,
                    COUNT(gp.game_id) games,
                    (
                        SELECT COUNT(*)
                        FROM game_players
                        WHERE player_id=p.id AND base_player=1
                    ) AS games_base,
                    ROUND(SUM (gp.time_played / 60.0)) AS time_played,
                    (
                        SELECT COUNT(*)
                        FROM game_log
                        WHERE player_from=p.id AND type=\(GameLogType.Goal.rawValue)
                    ) AS goals,
                    (
                        SELECT COUNT(*)
                        FROM game_log
                        WHERE player_from=p.id AND type=\(GameLogType.Pass.rawValue)
                    ) AS passes,
                    (
                        SELECT COUNT(*)
                        FROM game_log
                        WHERE player_from=p.id AND type=\(GameLogType.Penalty.rawValue)
                    ) AS yellow,
                    0 AS red
                FROM players p, game_players gp
                WHERE p.team_id=\(team_id) AND gp.player_id=p.id
                GROUP by 1, 2
                ORDER BY p.number ASC
                """)
            for _ in stmt
            {
                let item = TeamPlayerRow()
                item.number = stmt.row[0]
                item.player = stmt.row[1]
                item.games = stmt.row[2]
                item.games_base = stmt.row[3]
                item.time = stmt.row[4]
                item.goals = stmt.row[5]
                item.passes = stmt.row[6]
                item.yellow = stmt.row[7]
                item.red = stmt.row[8]
                data.append(item)
            }
        }
        catch
        {
            print("Failed to gather team player data")
        }
        return data
    }
}
