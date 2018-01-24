//
//  ViewController.swift
//  PD2
//
//  Created by Gusts Kaksis on 16/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Cocoa

enum ResultsView
{
    case leagueStats
    case topPlayers
    case topGoalies
    case topRudest
    case teams
    case topReferees
}

class ViewController: NSViewController {

    @IBOutlet weak var lblStatus: NSTextField!
    @IBOutlet weak var tblResults: NSTableView!
    var theDataSource: DataView!
    
    var viewMode:ResultsView = .leagueStats
    
    func update()
    {
        if tournament != nil
        {
            lblStatus.stringValue = "Database ready"
            updateViewMode(viewMode)
        }
    }
    
    func updateViewMode(_ mode:ResultsView)
    {
        if tournament == nil
        {
            return
        }
        
        switch mode
        {
        case .leagueStats:
            theDataSource = LeagueStatisticsView((tournament?.getLeagueStatistics())!)
            break
        case .topPlayers:
            theDataSource = TopPlayerView((tournament?.getTopPlayers())!)
            break
        case .topGoalies:
            theDataSource = TopGoalkeeperView((tournament?.getTopGoalkeepers())!)
            break
        case .topRudest:
            theDataSource = TopRudestPlayerView((tournament?.getTopRudestPlayers())!)
            break
        case .teams:
            theDataSource = TeamPlayerView((tournament?.getTeamPlayers())!)
            break
        case .topReferees:
            theDataSource = TopRefereesView((tournament?.getTopReferees())!)
            break
        }
        tblResults.dataSource = theDataSource
        tblResults.delegate = theDataSource
        theDataSource.updateColumns(tblResults)
        tblResults.reloadData()
        viewMode = mode
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        update()
    }
    
    override func viewWillDisappear() {
        NSApp.terminate(self)
    }

    override var representedObject: Any? {
        didSet {
        
        }
    }

}
