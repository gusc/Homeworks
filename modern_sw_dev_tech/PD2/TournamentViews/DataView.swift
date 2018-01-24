//
//  DataView.swift
//  PD2
//
//  Created by Gusts Kaksis on 19/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Foundation
import AppKit

// Base class for data source and delegate classes
class DataView : NSObject, NSTableViewDataSource, NSTableViewDelegate
{
    func updateColumns(_ table:NSTableView)
    {
        // TODO: hide unnecessary columns
    }
    
    func numberOfRows(in tableView: NSTableView) -> Int
    {
        return 0
    }
    
    func tableView(_ tableView: NSTableView, viewFor tableColumn: NSTableColumn?, row: Int) -> NSView?
    {
        let val = self.tableView(tableView, objectValueFor: tableColumn, row: row) as! String
        if let cell = tableView.makeView(withIdentifier: NSUserInterfaceItemIdentifier(rawValue: "TextCellID"), owner: self) as? NSTableCellView {
            cell.textField?.stringValue = val
            return cell
        }
        return nil
    }
    
    func tableView(_ tableView: NSTableView, objectValueFor tableColumn: NSTableColumn?, row: Int) -> Any?
    {
        return "-"
    }
}

