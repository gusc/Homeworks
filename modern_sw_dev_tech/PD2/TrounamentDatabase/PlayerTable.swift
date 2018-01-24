//
//  PlayerTable.swift
//  PD2
//
//  Created by Gusts Kaksis on 18/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Foundation
import SQLite

class PlayerTable
{
    static let table = Table("players")
    static let id = Expression<Int64>("id")
    static let name = Expression<String?>("name")
    static let team_id = Expression<Int64>("team_id")
    static let number = Expression<Int64>("number")
    static let position = Expression<String?>("position")

    static func updateDb(db:Connection, player:Player)
    {
        do
        {
            player.id = try db.run(table.insert(
                team_id <- player.team_id,
                name <- player.firstname + " " + player.lastname,
                number <- player.number,
                position <- player.position
            ))
        }
        catch
        {
            do
            {
                let player_query = table.filter(team_id == player.team_id && number == player.number)
                let presponse = try db.pluck(player_query.select(id))
                player.id = try presponse?.get(id)
            }
            catch
            {
                print("Something wrong with the player")
            }
        }
    }

    static func createDb(_ db:Connection) throws
    {
        // Create "players" table if not exists
        try db.run(table.create(ifNotExists: true, block: { t in
            t.column(id, primaryKey: .autoincrement)
            t.column(team_id)
            t.column(name)
            t.column(number)
            t.column(position)
            t.foreignKey(team_id, references: TeamTable.table, TeamTable.id, delete: .cascade)
            t.unique(team_id, number)
        }))
    }
}
