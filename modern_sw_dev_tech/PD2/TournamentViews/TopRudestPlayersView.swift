//
//  TopRudestPlayers.swift
//  PD2
//
//  Created by Gusts Kaksis on 19/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Foundation
import AppKit

class TopRudeRow
{
    var player:String?
    var team:String?
    var penalties:Int64 = 0
    
    init()
    {
    }
}

fileprivate let ColWidths = [40, 200, 200, 80, 0, 0, 0, 0, 0]
fileprivate let ColHeaders = ["No.", "Player", "Team", "Pnalties", "", "", "", "", ""]

class TopRudestPlayerView : DataView
{
    var data:[TopRudeRow]?
    
    init(_ withData:[TopRudeRow])
    {
        data = withData
    }
    
    override func updateColumns(_ table:NSTableView)
    {
        var idx = 0
        for col in table.tableColumns
        {
            col.width = CGFloat(ColWidths[idx])
            col.headerCell.title = ColHeaders[idx]
            if col.width > 0
            {
                col.isHidden = false
            }
            else
            {
                col.isHidden = true
            }
            idx += 1
        }
    }
    
    override func numberOfRows(in tableView: NSTableView) -> Int
    {
        return data?.count ?? 0
    }
    
    override func tableView(_ tableView: NSTableView, objectValueFor tableColumn: NSTableColumn?, row: Int) -> Any?
    {
        guard let item = data?[row]
            else
        {
            return "?"
        }
        
        if tableColumn == tableView.tableColumns[0]
        {
            return String(row + 1)
        }
        else if tableColumn == tableView.tableColumns[1]
        {
            return item.player!
        }
        else if tableColumn == tableView.tableColumns[2]
        {
            return item.team!
        }
        else if tableColumn == tableView.tableColumns[3]
        {
            return String(item.penalties)
        }
        else
        {
            return ""
        }
    }
}
