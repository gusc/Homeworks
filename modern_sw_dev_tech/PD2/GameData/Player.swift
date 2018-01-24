//
//  Player.swift
//  PD2
//
//  Created by Gusts Kaksis on 18/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Foundation
import XMLMapper
import ObjectMapper

/// A base class representing a person
class Person : XMLMappable, Mappable
{
    var nodeName:String!

    var id:Int64!

    var firstname:String!
    var lastname:String!

    required init(map:XMLMap) {

    }
    required init(map:Map) {

    }

    func mapping(map:XMLMap) {
        firstname <- map.attributes["Vards"]
        lastname <- map.attributes["Uzvards"]
    }
    func mapping(map:Map) {
        firstname <- map["Vards"]
        lastname <- map["Uzvards"]
    }
}

/// Team player class
class Player : Person
{
    var team_id:Int64!

    var position:String!
    var number:Int64!

    override func mapping(map:XMLMap) {
        position <- map.attributes["Loma"]
        firstname <- map.attributes["Vards"]
        lastname <- map.attributes["Uzvards"]
        number <- map.attributes["Nr"]
    }
    override func mapping(map:Map) {
        position <- map["Loma"]
        firstname <- map["Vards"]
        lastname <- map["Uzvards"]
        number <- map["Nr"]
    }
}

/// Simple player number attribute mapper
class PlayerNo : XMLMappable, Mappable
{
    var nodeName:String!

    var number:Int64!

    required init(map:XMLMap) {

    }
    required init(map:Map) {

    }

    func mapping(map:XMLMap) {
        number <- map.attributes["Nr"]
    }
    func mapping(map:Map) {
        number <- map["Nr"]
    }
}

/// Referee class
class Referee : Person
{
    var game_id:Int64!
    var position:String!
}
