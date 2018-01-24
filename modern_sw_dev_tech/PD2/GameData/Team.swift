//
//  Team.swift
//  PD2
//
//  Created by Gusts Kaksis on 18/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Foundation
import XMLMapper
import ObjectMapper

/// A class representing a team
class Team : XMLMappable, Mappable
{
    var nodeName:String!

    var id:Int64!

    var name:String!
    var players:[Player]!
    var game_players:[PlayerNo]!

    var change_log:[LogChange]!
    var penalty_log:[LogPenalty]!
    var goal_log:[LogGoal]!
    // The XML ORM is stupidand can't stuff a single element into an array
    // JSON is legit, because there is no array
    var change_log1:LogChange!
    var penalty_log1:LogPenalty!
    var goal_log1:LogGoal!

    required init(map:XMLMap) {

    }
    required init(map:Map) {

    }
    func mapping(map:XMLMap) {
        name <- map.attributes["Nosaukums"]
        players <- map["Speletaji.Speletajs"]
        game_players <- map["Pamatsastavs.Speletajs"]
        change_log <- map["Mainas.Maina"]
        penalty_log <- map["Sodi.Sods"]
        goal_log <- map ["Varti.VG"]
        change_log1 <- map["Mainas.Maina"]
        penalty_log1 <- map["Sodi.Sods"]
        goal_log1 <- map ["Varti.VG"]
        fixUpMappings()
    }
    func mapping(map:Map) {
        name <- map["Nosaukums"]
        players <- map["Speletaji.Speletajs"]
        game_players <- map["Pamatsastavs.Speletajs"]
        change_log <- map["Mainas.Maina"]
        penalty_log <- map["Sodi.Sods"]
        goal_log <- map ["Varti.VG"]
        change_log1 <- map["Mainas.Maina"]
        penalty_log1 <- map["Sodi.Sods"]
        goal_log1 <- map ["Varti.VG"]
        fixUpMappings()
    }
    
    func findByNumber(_ num:Int64) -> Player!
    {
        for player in players
        {
            if player.number == num
            {
                return player
            }
        }
        return nil
    }
    
    private func fixUpMappings()
    {
        if change_log == nil
        {
            change_log = []
            if change_log1 != nil
            {
                change_log.append(change_log1)
            }
        }
        if goal_log == nil
        {
            goal_log = []
            if goal_log1 != nil
            {
                goal_log.append(goal_log1)
            }
        }
        if penalty_log == nil
        {
            penalty_log = []
            if penalty_log1 != nil
            {
                penalty_log.append(penalty_log1)
            }
        }
    }
}

//
// Helper classes used for XML/JSON mapping
//


/// Mapper transformer class to transform MM:SS time format into Date object
class LogTimeTransform: XMLTransformType, TransformType
{
    public typealias Object = Date
    public typealias XML = String
    public typealias JSON = String

    func transformFromXML(_ value:Any?) -> Date?
    {
        if let timeString = value as? String
        {
            let parts = timeString.split(separator: ":")
            if (parts.count == 2)
            {
                var time = Double(parts[1])!
                time += (Double(parts[0])! * 60)
                return Date(timeIntervalSince1970: time)
            }
        }
        return nil
    }

    func transformToXML(_ value:Date?) -> String?
    {
        if let date = value
        {
            let time = UInt64(date.timeIntervalSince1970)
            return String(format: "%llu:%llu", [time / 60, time % 60])
        }
        return nil
    }

    func transformFromJSON(_ value:Any?) -> Date?
    {
        return transformFromXML(value)
    }

    func transformToJSON(_ value:Date?) -> String?
    {
        return transformToXML(value)
    }
}

/// Helper class that maps JSON and XML data of a player change
class LogChange : XMLMappable, Mappable
{
    var nodeName:String!
    
    var time:Date!
    var number_from:Int64!
    var number_to:Int64!
    
    required init(map:XMLMap) {
        
    }
    required init(map:Map) {
        
    }
    
    func mapping(map:XMLMap) {
        time <- (map.attributes["Laiks"], LogTimeTransform())
        number_from <- map.attributes["Nr1"]
        number_to <- map.attributes["Nr2"]
    }
    func mapping(map:Map) {
        time <- (map["Laiks"], LogTimeTransform())
        number_from <- map["Nr1"]
        number_to <- map["Nr2"]
    }
}

/// Helper class that maps JSON and XML data of a penalty
class LogPenalty : XMLMappable, Mappable
{
    var nodeName:String!
    
    var time:Date!
    var number:Int64!
    
    required init(map:XMLMap) {
        
    }
    required init(map:Map) {
        
    }
    
    func mapping(map:XMLMap) {
        time <- (map.attributes["Laiks"], LogTimeTransform())
        number <- map.attributes["Nr"]
    }
    func mapping(map:Map) {
        time <- (map["Laiks"], LogTimeTransform())
        number <- map["Nr"]
    }
}

/// Helper class that maps JSON and XML data of a goal
class LogGoal : XMLMappable, Mappable
{
    var nodeName:String!
    
    var time:Date!
    var number:Int64!
    var type:String!
    var passers:[PlayerNo]!
    var passers1:PlayerNo!
    
    required init(map:XMLMap) {
        
    }
    required init(map:Map) {
        
    }
    
    func mapping(map:XMLMap) {
        time <- (map.attributes["Laiks"], LogTimeTransform())
        number <- map.attributes["Nr"]
        type <- map.attributes["Sitiens"]
        passers <- map["P"]
        passers1 <- map["P"]
        fixUpMappings()
    }
    func mapping(map:Map) {
        time <- (map["Laiks"], LogTimeTransform())
        number <- map["Nr"]
        type <- map["Sitiens"]
        passers <- map["P"]
        passers1 <- map["P"]
        fixUpMappings()
    }
    
    private func fixUpMappings()
    {
        if passers == nil
        {
            passers = []
            if passers1 != nil
            {
                passers.append(passers1)
            }
        }
    }
}
