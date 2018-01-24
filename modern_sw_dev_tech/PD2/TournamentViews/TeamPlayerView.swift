//
//  TeamPlayerView.swift
//  PD2
//
//  Created by Gusts Kaksis on 19/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Foundation
import AppKit

class TeamPlayerRow
{
    var number:Int64 = 0
    var player:String?
    var games:Int64 = 0
    var games_base:Int64 = 0
    var time:Double = 0.0
    var goals:Int64 = 0
    var passes:Int64 = 0
    var yellow:Int64 = 0
    var red:Int64 = 0
    
    init()
    {
    }
}

fileprivate let ColWidths = [40, 200, 40, 40, 40, 40, 40, 40, 40]
fileprivate let ColHeaders = ["No.", "Player", "GP", "GPT", "Time", "G", "P", "Y", "R"]

class TeamPlayerView : DataView
{
    var data:[TeamPlayerRow]?
    
    init(_ withData:[TeamPlayerRow])
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
            return String(item.number)
        }
        else if tableColumn == tableView.tableColumns[1]
        {
            return item.player!
        }
        else if tableColumn == tableView.tableColumns[2]
        {
            return String(item.games)
        }
        else if tableColumn == tableView.tableColumns[3]
        {
            return String(item.games_base)
        }
        else if tableColumn == tableView.tableColumns[4]
        {
            return String(item.time)
        }
        else if tableColumn == tableView.tableColumns[5]
        {
            return String(item.goals)
        }
        else if tableColumn == tableView.tableColumns[6]
        {
            return String(item.passes)
        }
        else if tableColumn == tableView.tableColumns[7]
        {
            return String(item.yellow)
        }
        else if tableColumn == tableView.tableColumns[8]
        {
            if item.yellow > 0 && item.yellow % 2 == 0
            {
                return String(item.yellow / 2)
            }
            return "0"
        }
        else
        {
            return ""
        }
    }
}
