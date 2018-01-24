//
//  DataFile.swift
//  PD2
//
//  Created by Gusts Kaksis on 17/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Foundation
import XMLMapper
import ObjectMapper

// A class that's resposible for reading import data from JSON and XML files
// and mapping the data to objects
class GameDataFile : Mappable
{
    var game:Game!
    
    required init(map:Map) {
        
    }
    
    func mapping(map:Map) {
        game <- map["Spele"]
    }
    
    static func ParseFile(path:URL) throws -> Game?
    {
        var game:Game? = nil
        if path.pathExtension == "xml"
        {
            let xmlString = try String(contentsOf: path, encoding: String.Encoding.utf8)
            game = Game(XMLString: xmlString)
        }
        else if path.pathExtension == "json"
        {
            let jsonString = try String(contentsOf: path, encoding: String.Encoding.ascii)
            game = GameDataFile(JSONString: jsonString)?.game
        }
        else
        {
            print("Unkown format")
        }
        return game
    }
}
