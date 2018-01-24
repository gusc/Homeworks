//
//  Team.swift
//  PD2
//
//  Created by Gusts Kaksis on 18/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Foundation
import SQLite

class TeamTable
{
    static let table = Table("teams")
    static let id = Expression<Int64>("id")
    static let name = Expression<String?>("name")

    static func getList(_ db:Connection) -> [Int64 : String]
    {
        var list:[Int64 : String] = [Int64 : String]()
        do
        {
            for team in try db.prepare(table.select(id, name).order(name.asc))
            {
                list[try team.get(id)] = try team.get(name)
            }
        }
        catch
        {
            print("Failed to list teams")
        }
        return list
    }
    
    static func updateDb(db:Connection, team:Team)
    {
        do
        {
            // Let's try to insert the team
            team.id = try db.run(table.insert(name <- team.name))
        }
        catch
        {
            // Team already exists - nothing to update, just gather some datas
            do
            {
                let team_query = table.filter(name == team.name)
                let response = try db.pluck(team_query.select(id))
                team.id = try response?.get(id)
            }
            catch
            {
                print("Selecting a team failed")
            }
        }

        // Update the team player data
        for player in team.players
        {
            player.team_id = team.id
            PlayerTable.updateDb(db: db, player: player)
        }
    }

    static func createDb(_ db:Connection) throws
    {
        // Create "teams" table if not exists
        try db.run(table.create(ifNotExists: true, block: { t in
            t.column(id, primaryKey: .autoincrement)
            t.column(name, unique: true)
        }))
    }
}
