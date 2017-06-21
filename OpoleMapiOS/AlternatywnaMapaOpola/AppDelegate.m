//
//  AppDelegate.m
//  AlternatywnaMapaOpola
//
//  Created by Grzegorz Frydrych on 23.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import "AppDelegate.h"
#import "CategoriesViewController.h"
#import "AMODataManager.h"
#import "MainViewController.h"
#import "MapViewController.h"
#import "PlacesViewController.h"
#import "MainSplitViewController.h"


@implementation AppDelegate

MainSplitViewController *_splitViewController;
int lastCategory, lastPoi;

- (void)dealloc
{
    [_window release];
    [_viewController release];
    [super dealloc];
}

MapViewController *mvc;
bool isPad;
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    [self loadPrefs];
    
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {
        _splitViewController = (MainSplitViewController *)self.window.rootViewController;
        
        UINavigationController *navi = [_splitViewController.viewControllers objectAtIndex:1];
        MapViewController *map = [navi.viewControllers objectAtIndex:0];
        
        NSDictionary *place = [[[AMODataManager shared] placesForCategoryId:lastCategory] objectAtIndex:lastPoi];
        [map initWithPlace:place category:lastCategory];
        
        isPad = true;
        
        self.window.rootViewController = _splitViewController;
        _splitViewController.delegate = map;
        
        return YES;
    } else {
        self.window = [[[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]] autorelease];
        CategoriesViewController *vc = [[CategoriesViewController alloc] init];
        self.viewController = [[[UINavigationController alloc] initWithRootViewController:vc] autorelease];
        self.viewController.navigationBar.tintColor = [AMODataManager shared].colorActionBar;
        [vc release];
        
        self.window.rootViewController = self.viewController;
        [self.window makeKeyAndVisible];
        
        isPad = false;
        
        return YES;
    }
    
}

+(bool)getIsPad
{
    return isPad;
}

+(UISplitViewController *)getSVC
{
    return _splitViewController;
}

+(void)saveLastCategory:(int)categoryId
{
    lastCategory = categoryId;
}

+(void)saveLastPoi:(int)poiId
{
    lastPoi = poiId;
}

-(void)loadPrefs
{
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    lastCategory = [prefs integerForKey:@"lastCategory"];
    lastPoi = [prefs integerForKey:@"lastPoi"];
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
