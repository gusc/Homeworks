//
//  GameLogTable.swift
//  PD2
//
//  Created by Gusts Kaksis on 18/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Foundation
import SQLite

class GameLogTable
{
    static let table = Table("game_log")
    static let game_id = Expression<Int64>("game_id")
    static let event_time = Expression<Date?>("event_time")
    static let type = Expression<Int64>("type")
    static let player_from = Expression<Int64>("player_from")
    static let player_to = Expression<Int64?>("player_to")
    static let info = Expression<String?>("info")

    static func updateDb(db:Connection, log:GameLog)
    {
        do
        {
            // Clear old entries
            try db.run(table.filter(game_id == log.game.id).delete())
            // Re-create from beginning
            for e in log.events
            {
                var pto:Int64? = nil
                if e.number_to != nil
                {
                    pto = log.game.teams[e.team_index].findByNumber(e.number_to!).id
                }
                try db.run(table.insert(
                    game_id <- log.game.id,
                    event_time <- log.game.start_date.addingTimeInterval(e.time.timeIntervalSince1970),
                    type <- e.type.rawValue,
                    player_from <- log.game.teams[e.team_index].findByNumber(e.number_from).id,
                    player_to <- pto,
                    info <- e.info
                ))
            }
        }
        catch
        {
            print("Unable to set game log")
        }
    }

    static func createDb(_ db:Connection) throws
    {
        // Create "game_log" table if not exists
        try db.run(table.create(ifNotExists: true, block: { t in
            t.column(game_id)
            t.column(event_time)
            t.column(type)
            t.column(player_from)
            t.column(player_to)
            t.column(info)
            t.foreignKey(game_id, references: GameTable.table, GameTable.id, delete: .cascade)
            t.foreignKey(player_from, references: PlayerTable.table, PlayerTable.id, delete: .cascade)
            t.foreignKey(player_to, references: PlayerTable.table, PlayerTable.id, delete: .setNull)
        }))
    }
}
