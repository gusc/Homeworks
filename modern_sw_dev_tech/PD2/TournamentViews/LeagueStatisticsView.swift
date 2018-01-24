//
//  LeagueStatisticsView.swift
//  PD2
//
//  Created by Gusts Kaksis on 19/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Foundation
import AppKit

class LeagueStatsRow
{
    var team:String?
    var points:Int64 = 0
    var wins:Int64 = 0
    var losses:Int64 = 0
    var wins_ex:Int64 = 0
    var losses_ex:Int64 = 0
    var goals:Int64 = 0
    var against:Int64 = 0
    
    init()
    {
    }
}

fileprivate let ColWidths = [40, 400, 40, 40, 40, 40, 40, 40, 40]
fileprivate let ColHeaders = ["No.", "Team", "Points", "W", "L", "WO", "LO", "GF", "GA"]

class LeagueStatisticsView : DataView
{
    var data:[LeagueStatsRow]!
    
    init(_ withData:[LeagueStatsRow])
    {
        data = withData
    }
    
    override func updateColumns(_ table:NSTableView)
    {
        var idx = 0
        for col in table.tableColumns
        {
            col.isHidden = false
            col.width = CGFloat(ColWidths[idx])
            col.headerCell.title = ColHeaders[idx]
            idx += 1
        }
    }
    
    func tableView(_ tableView: NSTableView, didClick tableColumn: NSTableColumn)
    {
        // TODO: update sort column if ever necessary
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
            return item.team!
        }
        else if tableColumn == tableView.tableColumns[2]
        {
            return String(item.points)
        }
        else if tableColumn == tableView.tableColumns[3]
        {
            return String(item.wins)
        }
        else if tableColumn == tableView.tableColumns[4]
        {
            return String(item.losses)
        }
        else if tableColumn == tableView.tableColumns[5]
        {
            return String(item.wins_ex)
        }
        else if tableColumn == tableView.tableColumns[6]
        {
            return String(item.losses_ex)
        }
        else if tableColumn == tableView.tableColumns[7]
        {
            return String(item.goals)
        }
        else if tableColumn == tableView.tableColumns[8]
        {
            return String(item.against)
        }
        else
        {
            return "-"
        }
    }

}
