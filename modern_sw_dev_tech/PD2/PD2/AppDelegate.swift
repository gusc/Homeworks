//
//  AppDelegate.swift
//  PD2
//
//  Created by Gusts Kaksis on 16/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

import Cocoa

var tournament:TournamentDatabase?

@NSApplicationMain
class AppDelegate: NSObject, NSApplicationDelegate
{
    @IBOutlet weak var teamList: NSMenuItem!
    
    @IBAction func viewLeagueStats(_ sender: Any) {
        updateViewMode(ResultsView.leagueStats)
    }
    
    @IBAction func viewTopPlayers(_ sender: Any) {
        updateViewMode(ResultsView.topPlayers)
    }
    
    @IBAction func viewTopGoalies(_ sender: Any) {
        updateViewMode(ResultsView.topGoalies)
    }
    
    @IBAction func viewTopRudest(_ sender: Any) {
        updateViewMode(ResultsView.topRudest)
    }
    
    @IBAction func viewTopReferees(_ sender: Any) {
        updateViewMode(ResultsView.topReferees)
    }
    
    @IBAction func newDb(_ sender: Any)
    {
        let panel = NSSavePanel()
        panel.allowedFileTypes = ["db"]
        panel.begin { (result) in
            if result == NSApplication.ModalResponse.OK
            {
                let newUrl = panel.url!
                do
                {
                    tournament = try TournamentDatabase(path: newUrl.absoluteString)
                    self.updateMainWin()
                    UserDefaults.standard.set(newUrl, forKey: "lastDatabase")
                }
                catch
                {
                    print("There was an error loading tournament database")
                }
            }
        }
    }
    
    @IBAction func openDb(_ sender: Any)
    {
        let panel = NSOpenPanel()
        panel.allowsMultipleSelection = false
        panel.canChooseDirectories = false
        panel.canChooseFiles = true
        panel.allowedFileTypes = ["db"]
        panel.begin { (result) in
            if result == NSApplication.ModalResponse.OK
            {
                let newUrl = panel.url!
                do
                {
                    tournament = try TournamentDatabase(path: newUrl.absoluteString)
                    self.updateMainWin()
                    UserDefaults.standard.set(newUrl, forKey: "lastDatabase")
                }
                catch
                {
                    print("There was an error loading tournament database")
                }
            }
        }
    }
    
    @IBAction func importData(_ sender: Any)
    {
        let panel = NSOpenPanel()
        panel.canChooseFiles = true
        panel.allowsMultipleSelection = true
        panel.begin { (result) in
            if result == NSApplication.ModalResponse.OK
            {
                for newUrl in panel.urls
                {
                    do
                    {
                        let game = try GameDataFile.ParseFile(path: newUrl)
                        try tournament?.addGame(game!)
                        self.updateMainWin()
                    }
                    catch
                    {
                        print(error)
                    }
                }
            }
        }
    }
    
    func updateMainWin()
    {
        let win = NSApplication.shared.mainWindow
        if win?.contentViewController is ViewController
        {
            let wc = win?.contentViewController as! ViewController
            wc.update()
        }
        populateTeamList()
    }
    
    func updateViewMode(_ mode:ResultsView)
    {
        let win = NSApplication.shared.mainWindow
        if win?.contentViewController is ViewController
        {
            let wc = win?.contentViewController as! ViewController
            wc.updateViewMode(mode)
        }
    }
    
    func populateTeamList()
    {
        teamList.submenu?.removeAllItems()
        let teams = tournament?.getTeams()
        if teams != nil
        {
            for (_, name) in teams!
            {
                teamList.submenu?.addItem(withTitle: name, action:#selector(chooseTeam) , keyEquivalent: "")
            }
        }
    }
    
    @objc
    func chooseTeam(sender:NSMenuItem)
    {
        let name_sel = sender.title
        let teams = tournament?.getTeams()
        for (id, name) in teams!
        {
            if name == name_sel
            {
                tournament?.selectTeamId(id)
                updateViewMode(ResultsView.teams)
                return
            }
        }
    }
    
    func applicationDidFinishLaunching(_ aNotification: Notification)
    {
        let lastUrl = UserDefaults.standard.url(forKey: "lastDatabase")
        if lastUrl != nil
        {
            do
            {
                tournament = try TournamentDatabase(path: (lastUrl?.absoluteString)!)
            }
            catch
            {
                print("There was an error loading tournament database")
                UserDefaults.standard.removeObject(forKey: "lastDatabase")
            }
        }
        updateMainWin()
    }

    func applicationWillTerminate(_ aNotification: Notification)
    {
        
    }
}
