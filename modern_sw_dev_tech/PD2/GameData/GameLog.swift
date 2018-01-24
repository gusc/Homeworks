//
//  GameLog.swift
//  PD2
//
//  Created by Gusts Kaksis on 18/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Foundation

enum GameLogType : Int64
{
    case Penalty = 1
    case Goal
    case GoalPenalty
    case Pass
    case PlayerChange
}

/// Class representing a single event in game
class GameLogEvent
{
    var id:Int64!
    
    var type:GameLogType!
    var time:Date!
    var team_index:Int!
    var number_from:Int64!
    var number_to:Int64?
    var info:String?

    // Map player change
    init(team_index:Int, change:LogChange)
    {

        self.time = change.time
        self.team_index = team_index
        self.number_from = change.number_from
        self.number_to = change.number_to
        self.type = .PlayerChange
    }

    // Map goal
    init(team_index:Int, goal:LogGoal)
    {
        self.time = goal.time
        self.team_index = team_index
        self.number_from = goal.number
        if goal.type == "J"
        {
            self.type = .GoalPenalty
        }
        else
        {
            self.type = .Goal
        }
    }

    // Map pass
    init(team_index:Int, time:Date, number_from:Int64, number_to:Int64)
    {
        self.time = time
        self.team_index = team_index
        self.number_from = number_from
        self.number_to = number_to
        self.type = .Pass
    }

    // Map penalty
    init(team_index:Int, penalty:LogPenalty)
    {
        self.time = penalty.time
        self.team_index = team_index
        self.number_from = penalty.number
        self.type = .Penalty
    }
}

/// Class maintaining a full game log, sorted in ascending order by the event time
class GameLog
{
    var game:Game!
    var events:[GameLogEvent]!

    required init(_ game:Game)
    {
        self.game = game
        events = [GameLogEvent]()
    }

    func addEvent(_ event:GameLogEvent)
    {
        events.append(event)
        events.sort { (a, b) -> Bool in
            return a.time < b.time
        }
    }
}
