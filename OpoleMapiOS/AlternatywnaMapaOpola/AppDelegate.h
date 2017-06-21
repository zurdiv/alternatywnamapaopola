//
//  AppDelegate.h
//  AlternatywnaMapaOpola
//
//  Created by Grzegorz Frydrych on 23.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MapViewController.h"



@interface AppDelegate : UIResponder <UIApplicationDelegate> 
+(UISplitViewController*) getSVC;
+(bool) getIsPad;
+(void)saveLastCategory:(int)categoryId;
+(void)saveLastPoi:(int)poiId;
//@property UISplitViewController *splitViewController;
@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) UINavigationController *viewController;
//@property (nonatomic, strong) UIView *splitViewController;
@end
