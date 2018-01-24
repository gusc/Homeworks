//
//  Game.swift
//  PD2
//
//  Created by Gusts Kaksis on 18/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Foundation
import XMLMapper
import ObjectMapper

/// Main game event class
class Game : XMLMappable, Mappable
{
    var nodeName:String!

    var id:Int64!
    var game_log:GameLog!
    var game_stats:[GameStats]!
    
    var teams:[Team]!
    var venue:String!
    var start_date:Date!
    var end_date:Date!
    var num_attendees:Int64!
    var main_referee:Referee!
    var line_referees:[Referee]!
    // Same as with the team data
    var line_referees1:Referee!
    
    required init(map: XMLMap) {
        
    }
    required init(map: Map) {
        
    }

    // Move all the game log related data from Team to Game
    private func processGameLog()
    {
        game_log = GameLog(self)
        game_stats = [GameStats]()
        
        let game_end_time = Date(timeIntervalSince1970: 60 * 60)
        
        var idx = 0
        for team in teams
        {
            let gs = GameStats()
            gs.time_played = 60.0 * 60.0
            
            // Process changes
            for change in team.change_log
            {
                let le = GameLogEvent(team_index: idx, change: change)
                game_log.addEvent(le)
            }
            
            // Process goals
            for goal in team.goal_log
            {
                let le = GameLogEvent(team_index: idx, goal: goal)
                game_log.addEvent(le)
                for pass in goal.passers
                {
                    game_log.addEvent(GameLogEvent(
                        team_index: idx,
                        time: goal.time,
                        number_from: pass.number,
                        number_to: goal.number
                    ))
                }
                
                if le.time < game_end_time
                {
                    gs.goals = gs.goals + 1
                }
                else
                {
                    gs.goals_ex = gs.goals_ex + 1
                    gs.time_played = le.time.timeIntervalSince1970
                }
            }
            
            // Process penalties
            for penalty in team.penalty_log
            {
                let le = GameLogEvent(team_index: idx, penalty: penalty)
                game_log.addEvent(le)
                
                if le.time < game_end_time
                {
                    gs.penalties = gs.penalties + 1
                }
                else
                {
                    gs.penalties_ex = gs.penalties_ex + 1
                }
            }
            
            game_stats.append(gs)
            idx += 1
        }
        
        // Fixup stats
        game_stats[1].against = game_stats[0].goals
        game_stats[1].against_ex = game_stats[0].goals_ex
        game_stats[0].against = game_stats[1].goals
        game_stats[0].against_ex = game_stats[1].goals_ex
        if game_stats[1].goals > game_stats[0].goals
        {
            game_stats[1].winner = true
            game_stats[1].points = 5
            game_stats[0].looser = true
            game_stats[0].points = 1
        }
        else if game_stats[1].goals < game_stats[0].goals
        {
            game_stats[0].winner = true
            game_stats[0].points = 5
            game_stats[1].looser = true
            game_stats[1].points = 1
        }
        else if game_stats[1].goals + game_stats[1].goals_ex > game_stats[0].goals + game_stats[0].goals_ex
        {
            game_stats[1].winner_ex = true
            game_stats[1].points = 3
            game_stats[0].looser_ex = true
            game_stats[0].points = 2
        }
        else if game_stats[1].goals + game_stats[1].goals_ex < game_stats[0].goals + game_stats[0].goals_ex
        {
            game_stats[0].winner_ex = true
            game_stats[0].points = 3
            game_stats[1].looser_ex = true
            game_stats[1].points = 2
        }
        if game_stats[0].time_played > game_stats[1].time_played
        {
            game_stats[1].time_played = game_stats[0].time_played
        }
        else
        {
            game_stats[0].time_played = game_stats[1].time_played
        }
        // Update game end time
        end_date = start_date.addingTimeInterval(game_stats[0].time_played)
    }

    func mapping(map: XMLMap) {
        teams <- map["Komanda"]
        venue <- map.attributes["Vieta"]
        start_date <- (map.attributes["Laiks"], XMLCustomDateFormatTransform(formatString: "yyyy/MM/dd", withLocaleIdentifier: "lv_LV"))
        num_attendees <- map.attributes["Skatitaji"]
        main_referee <- map["VT"]
        line_referees <- map["T"]
        line_referees1 <- map["T"]
        fixUpMappings()
        processGameLog()
    }
    func mapping(map: Map) {
        teams <- map["Komanda"]
        venue <- map["Vieta"]
        start_date <- (map["Laiks"], CustomDateFormatTransform(formatString: "yyyy/MM/dd"))
        num_attendees <- map["Skatitaji"]
        main_referee <- map["VT"]
        line_referees <- map["T"]
        line_referees1 <- map["T"]
        fixUpMappings()
        processGameLog()
    }
    
    private func fixUpMappings()
    {
        if main_referee != nil
        {
            main_referee.position = "VT"
        }
        if line_referees == nil
        {
            line_referees = []
            if line_referees1 != nil
            {
                line_referees.append(line_referees1)
            }
        }
        for ref in line_referees
        {
            ref.position = "T"
        }
    }
}

class GamePlayer
{
    var id:Int64!
    var game_id:Int64!
    var team_id:Int64!
    var base_player:Bool!
    var player:Player!
    var time_played:Double!
    
    init(_ from:Player)
    {
        self.id = from.id
        self.team_id = from.team_id
        self.player = from
        self.base_player = true
    }
}

class GameStats
{
    var team_id:Int64!
    var game_id:Int64!
    var points:Int64 = 0
    var goals:Int64 = 0
    var goals_ex:Int64 = 0
    var against:Int64 = 0
    var against_ex:Int64 = 0
    var penalties:Int64 = 0
    var penalties_ex:Int64 = 0
    var winner:Bool = false
    var winner_ex:Bool = false
    var looser:Bool = false
    var looser_ex:Bool = false
    var time_played:Double!
    
    init()
    {
    }
}
