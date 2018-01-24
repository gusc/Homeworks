//
//  Game.swift
//  PD2
//
//  Created by Gusts Kaksis on 18/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Foundation
import SQLite

class GameTable
{
    static let table = Table("games")
    static let id = Expression<Int64>("id")
    static let team_home = Expression<Int64>("team_home")
    static let team_away = Expression<Int64>("team_away")
    static let start_date = Expression<Date?>("start_date")
    static let end_date = Expression<Date?>("end_date")
    static let venue = Expression<String?>("venue")
    static let num_attendees = Expression<Int64>("num_attendees")

    static func updateDb(db:Connection, game:Game)
    {
        // Select game ID or insert a new game
        do
        {
            // Let's try to check if this is an existing game
            let query = table.select(id)
                             .filter(start_date == game.start_date &&
                                    ((team_home == game.teams[0].id && team_away == game.teams[1].id) ||
                                    (team_home == game.teams[1].id && team_away == game.teams[0].id)))
            let response = try db.pluck(query.select(id))
            game.id = try response?.get(id)
        }
        catch
        {
            print("Failed to select game")
        }
        if game.id == nil
        {
            do
            {
                game.id = try db.run(table.insert(
                    team_home <- game.teams[0].id,
                    team_away <- game.teams[1].id,
                    start_date <- game.start_date,
                    end_date <- game.end_date,
                    venue <- game.venue,
                    num_attendees <- game.num_attendees
                ))
            }
            catch
            {
                print("Failed to insert game")
            }
        }
        
        // Insert referees
        game.main_referee.game_id = game.id
        GameRefereeTable.updateDb(db: db, ref: game.main_referee)
        for ref in game.line_referees
        {
            ref.game_id = game.id
            GameRefereeTable.updateDb(db: db, ref: ref)
        }
    
        // Insert players
        var idx = 0
        for team in game.teams
        {
            var players = [GamePlayer]()
            
            for game_player in team.game_players
            {
                let gp = GamePlayer(team.findByNumber(game_player.number))
                gp.game_id = game.id
                gp.time_played = game.game_stats[idx].time_played // Default 60 minutes
                players.append(gp)
            }
            // Add also the changed players
            for change in team.change_log
            {
                let gp = GamePlayer(team.findByNumber(change.number_to))
                gp.game_id = game.id
                gp.base_player = false
                
                let game_time = game.game_stats[idx].time_played!
                let change_time = change.time.timeIntervalSince1970
                gp.time_played = game_time - change_time
                players.append(gp)
                
                // Update other player too
                let p = team.findByNumber(change.number_from)
                for op in players
                {
                    if op.player.number == p?.number
                    {
                        let player_time = game.game_stats[idx].time_played!
                        op.time_played = player_time - (game_time - change_time)
                    }
                }
            }
            
            // Update database
            for gp in players
            {
                GamePlayerTable.updateDb(db: db, player: gp)
            }
            
            idx += 1
        }
        
        idx = 0
        for stat in game.game_stats
        {
            stat.game_id = game.id
            stat.team_id = game.teams[idx].id
            idx += 1
        }
    }

    static func createDb(_ db:Connection) throws
    {
        // Create "games" table if not exists
        try db.run(table.create(ifNotExists: true, block: { t in
            t.column(id, primaryKey: .autoincrement)
            t.column(team_home)
            t.column(team_away)
            t.column(start_date)
            t.column(end_date)
            t.column(venue)
            t.column(num_attendees)
            t.foreignKey(team_home, references: TeamTable.table, TeamTable.id, delete: .cascade)
            t.foreignKey(team_away, references: TeamTable.table, TeamTable.id, delete: .cascade)
            t.unique(team_home, team_away, start_date)
            // TODO: unique constraint on team_home, team_away, venue, start_date
        }))
    }
}

class GameStatsTable
{
    static let table = Table("game_stats")
    static let team_id = Expression<Int64>("team_id")
    static let game_id = Expression<Int64>("game_id")
    static let points = Expression<Int64>("points")
    static let goals = Expression<Int64>("goals")
    static let against = Expression<Int64>("against")
    static let goals_ex = Expression<Int64>("goals_ex")
    static let against_ex = Expression<Int64>("against_ex")
    static let penalties = Expression<Int64>("penalties")
    static let penalties_ex = Expression<Int64>("penalties_ex")
    static let winner = Expression<Bool>("winner")
    static let winner_ex = Expression<Bool>("winner_ex")
    static let looser = Expression<Bool>("looser")
    static let looser_ex = Expression<Bool>("looser_ex")
    static let time_played = Expression<Double>("time_played")
    
    static func updateDb(db:Connection, stats:GameStats)
    {
        do
        {
            try db.run(table.insert(or: .replace,
                                    game_id <- stats.game_id,
                                    team_id <- stats.team_id,
                                    points <- stats.points,
                                    goals <- stats.goals,
                                    against <- stats.against,
                                    goals_ex <- stats.goals_ex,
                                    against_ex <- stats.against_ex,
                                    penalties <- stats.penalties,
                                    penalties_ex <- stats.penalties_ex,
                                    winner <- stats.winner,
                                    winner_ex <- stats.winner_ex,
                                    looser <- stats.looser,
                                    looser_ex <- stats.looser_ex,
                                    time_played <- stats.time_played
            ))
        }
        catch
        {
            print("Failed to insert game stats")
        }
    }
    
    static func createDb(_ db:Connection) throws
    {
        // Create "game_stats" table if not exists
        try db.run(table.create(ifNotExists: true, block: { t in
            t.column(game_id)
            t.column(team_id)
            t.column(points)
            t.column(goals)
            t.column(against)
            t.column(goals_ex)
            t.column(against_ex)
            t.column(penalties)
            t.column(penalties_ex)
            t.column(winner)
            t.column(winner_ex)
            t.column(looser)
            t.column(looser_ex)
            t.column(time_played)
            t.foreignKey(game_id, references: GameTable.table, GameTable.id, delete: .cascade)
            t.foreignKey(team_id, references: TeamTable.table, TeamTable.id, delete: .cascade)
            t.unique(game_id, team_id)
        }))
    }
}

class GamePlayerTable
{
    static let table = Table("game_players")
    static let team_id = Expression<Int64>("team_id")
    static let game_id = Expression<Int64>("game_id")
    static let player_id = Expression<Int64>("player_id")
    static let base_player = Expression<Bool>("base_player")
    static let time_played = Expression<Double>("time_played")
    
    static func updateDb(db:Connection, player:GamePlayer)
    {
        do
        {
            try db.run(table.insert(or: .ignore,
                                    game_id <- player.game_id,
                                    team_id <- player.team_id,
                                    player_id <- player.id,
                                    base_player <- player.base_player,
                                    time_played <- player.time_played
            ))
        }
        catch
        {
            print("Failed to insert game player")
        }
    }
    
    static func createDb(_ db:Connection) throws
    {
        // Create "game_players" table if not exists
        try db.run(table.create(ifNotExists: true, block: { t in
            t.column(game_id)
            t.column(team_id)
            t.column(player_id)
            t.column(base_player)
            t.column(time_played)
            t.foreignKey(game_id, references: GameTable.table, GameTable.id, delete: .cascade)
            t.foreignKey(team_id, references: TeamTable.table, TeamTable.id, delete: .cascade)
            t.foreignKey(player_id, references: PlayerTable.table, PlayerTable.id, delete: .cascade)
            t.unique(game_id, player_id)
        }))
    }
}

class GameRefereeTable
{
    static let table = Table("game_referees")
    static let id = Expression<Int64>("id")
    static let game_id = Expression<Int64>("game_id")
    static let name = Expression<String?>("name")
    static let position = Expression<String?>("position")
    
    static func updateDb(db:Connection, ref:Referee)
    {
        do
        {
            // Let's try to check if this is an existing referee
            let query = table.select(id)
                             .filter(game_id == ref.game_id &&
                                     name == ref.firstname + " " + ref.lastname &&
                                     position == ref.position)
            let response = try db.pluck(query.select(id))
            ref.id = try response?.get(id)
        }
        catch
        {
            print("Failed to select game referee")
        }
        if ref.id == nil
        {
            do
            {
                ref.id = try db.run(table.insert(
                    game_id <- ref.game_id,
                    name <- ref.firstname + " " + ref.lastname,
                    position <- ref.position
                ))
            }
            catch
            {
                print("Failed to insert game referee")
            }
        }
    }
    
    static func createDb(_ db:Connection) throws
    {
        // Create "game_referees" table if not exists
        try db.run(table.create(ifNotExists: true, block: { t in
            t.column(id, primaryKey: .autoincrement)
            t.column(game_id)
            t.column(name)
            t.column(position)
            t.foreignKey(game_id, references: GameTable.table, GameTable.id, delete: .cascade)
        }))
    }
}
