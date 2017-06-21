//
//  MapViewController.h
//  AlternatywnaMapaOpola
//
//  Created by Grzegorz Frydrych on 23.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MapMarkerView.h"
#import "GPSManager.h"

@class MapTilesView;


@interface MapViewController : UIViewController <UIScrollViewDelegate, MapMarkerDelegate, GPSManagerDelegate, UISplitViewControllerDelegate>
{
    IBOutlet UIScrollView *_uiMapScrollView;
    MapTilesView *_tilesView;
    
    NSDictionary *_place;
    int _categoryId;
    int _zoomLevel;
    float _lastScale;
    
    UIImageView *_user;
    double _xRatio;
    double _yRatio;
}

@property (nonatomic, retain) IBOutlet UINavigationItem *navBarItem;
@property (nonatomic, strong) UIPopoverController *popover;

- (id)initWithPlace:(NSDictionary *)place category:(int)categoryId;
- (id)initWithoutPlace;
- (IBAction)clickZoomIn:(id)sender;
- (IBAction)clickZoomOut:(id)sender;

@end
