//
//  MapViewController.m
//  AlternatywnaMapaOpola
//
//  Created by Grzegorz Frydrych on 23.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import "MapViewController.h"
#import "MapTilesView.h"
#import "PlaceInfoViewController_iPad.h"
#import "PlaceInfoViewController_iPhone.h"
#import "MapMarkerView.h"
#import "AMODataManager.h"
#import "GPSManager.h"
#import "AppDelegate.h"

#define USER_MARKER_WIDTH 32
#define USER_MARKER_HEIGHT 32


@implementation MapViewController
- (id)initWithPlace:(NSDictionary *)place category:(int)categoryId
{
    self = [super initWithNibName:NSStringFromClass([self class]) bundle:nil];
    if (self) {
        _place = place;
        _categoryId = categoryId;
        _lastScale = 1.0f;
    }
    return self;
}

- (id)initWithoutPlace
{
    self = [super initWithNibName:NSStringFromClass([self class]) bundle:nil];
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.title = NSLocalizedString(@"Map", nil);
    
    [GPSManager shared].delegate = self;
    [[GPSManager shared] startUpdating];

    _user = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, USER_MARKER_WIDTH, USER_MARKER_HEIGHT)];
    _user.image = [UIImage imageNamed:@"marker_user"];
    
    _uiMapScrollView.backgroundColor = [UIColor blackColor];
    _uiMapScrollView.decelerationRate = UIScrollViewDecelerationRateFast;
    
    [self setZoomLevel:1];
    
    CLLocationCoordinate2D coordinate = CLLocationCoordinate2DMake([[_place objectForKey:kLat] doubleValue], [[_place objectForKey:kLon] doubleValue]);
    [self centerAtCoordinate:coordinate];
    
    UIPinchGestureRecognizer *pinchGesture = [[UIPinchGestureRecognizer alloc] initWithTarget:self action:@selector(pinch:)];
    [_uiMapScrollView addGestureRecognizer:pinchGesture];
    
    //
    UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
    [button setFrame:CGRectMake(0.0f, 0.0f, 32.0f, 32.0f)];
    [button addTarget:self action:@selector(clickSetToUserPosition:) forControlEvents:UIControlEventTouchUpInside];
    [button setImage:[UIImage imageNamed:@"marker_user"] forState:UIControlStateNormal];
    
    UIBarButtonItem *userPosButton = [[UIBarButtonItem alloc] initWithCustomView:button];
    self.navigationItem.rightBarButtonItem = userPosButton;
    [userPosButton release];
    
    self.navigationController.navigationBar.tintColor = [AMODataManager shared].colorActionBar;
    //if(popover != nil) {
    //    [popover dismissPopoverAnimated:YES];
    //}
    if([AppDelegate getIsPad])
    {
        if ([[UIApplication sharedApplication] statusBarOrientation] == UIInterfaceOrientationPortrait)
        {

        }
    }
    
}

- (void)viewDidUnload
{
    [_uiMapScrollView release];
    _uiMapScrollView = nil;
    [super viewDidUnload];
}

- (void)dealloc
{
    [[GPSManager shared] stopUpdating];
    [_user release];
    [_uiMapScrollView release];
    [super dealloc];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark

- (void)pinch:(UIPinchGestureRecognizer *)sender
{
    double diff = [sender scale] - _lastScale;
    
    if (diff > 0.4) {
        
        [self clickZoomIn:nil];
        _lastScale = [sender scale];
        
    } else if (diff < -0.4) {
        
        [self clickZoomOut:nil];
        _lastScale = [sender scale];
    }
    
    if (sender.state == UIGestureRecognizerStateEnded) {
        _lastScale = 1.0;
    }
}

- (void)setZoomLevel:(int)zoomLevel
{
    _zoomLevel = zoomLevel;
    
    int width = MAP_WIDTH / pow(2, zoomLevel);
    int height = MAP_HEIGHT / pow(2, zoomLevel);
    
    _uiMapScrollView.contentSize = CGSizeMake(width, height);
    CGRect frame = CGRectMake(0, 0, width, height);
    
    if (_tilesView) {
        [_tilesView removeFromSuperview];
    }
    
    _tilesView = [[MapTilesView alloc] initWithFrame:frame];
    _tilesView.tileSize = 200;
    _tilesView.zoomLevel = _zoomLevel;
    
    [_uiMapScrollView addSubview:_tilesView];
    [_tilesView release];
    
    [self updateMarkers];
}

- (CLLocationCoordinate2D)locationOfCenter
{
    CGPoint center = CGPointMake(_uiMapScrollView.contentOffset.x + _uiMapScrollView.frame.size.width / 2,
                                 _uiMapScrollView.contentOffset.y + _uiMapScrollView.frame.size.height / 2);
    
    double lon = (center.x / _xRatio) + V_LEFT;
    double lat = -((center.y / _yRatio) - V_TOP);
    
    return CLLocationCoordinate2DMake(lat, lon);
}

- (IBAction)clickZoomIn:(id)sender
{
    CLLocationCoordinate2D coordinate = [self locationOfCenter];
    
    if (_zoomLevel - 1 >= 0) {
        [self setZoomLevel:_zoomLevel - 1];
        [self centerAtCoordinate:coordinate];
    }
}

- (IBAction)clickZoomOut:(id)sender
{
    CLLocationCoordinate2D coordinate = [self locationOfCenter];
    
    if (_zoomLevel + 1 <= 3) {
        [self setZoomLevel:_zoomLevel + 1];
        [self centerAtCoordinate:coordinate];
    }
}

- (void)clickSetToUserPosition:(id)sender
{
    CLLocationCoordinate2D coordinate = [GPSManager shared].currentLocation.coordinate;
    CGPoint point = [self pointFromLat:coordinate.latitude lon:coordinate.longitude];
    point = CGPointMake(point.x - _uiMapScrollView.frame.size.width / 2, point.y - _uiMapScrollView.frame.size.height / 2);
    
    point = [self pointInScrollViewRange:point];
    [_uiMapScrollView setContentOffset:point];
}

- (CGPoint)pointFromLat:(double)lat lon:(double)lon
{
    double xdegree = V_RIGHT - V_LEFT;
    double ydegree = V_TOP - V_BOTTOM;
    
    int mapWidth = MAP_WIDTH / pow(2, _zoomLevel);
    int mapHeight = MAP_HEIGHT / pow(2, _zoomLevel);
    
    _xRatio = mapWidth / xdegree;
    _yRatio = mapHeight / ydegree;
    
    double xdiff = lon - V_LEFT;
    double ydiff = V_TOP - lat;
    
    return CGPointMake(xdiff * _xRatio, ydiff * _yRatio);
}

- (CGPoint)pointInScrollViewRange:(CGPoint)point
{
    // utrzymanie w granicach widoczności mapy
    if (point.x < 0) {
        point.x = 0;
    }
    
    if (point.y < 0) {
        point.y = 0;
    }
    
    int maxWidth = _uiMapScrollView.contentSize.width - _uiMapScrollView.frame.size.width;
    int maxHeight = _uiMapScrollView.contentSize.height - _uiMapScrollView.frame.size.height;
    
    if (point.x > maxWidth) {
        point.x = maxWidth;
    }
    
    if (point.y > maxHeight) {
        point.y = maxHeight;
    }

    return point;
}

- (void)centerAtCoordinate:(CLLocationCoordinate2D)coordinate
{
    CGPoint point = [self pointFromLat:coordinate.latitude lon:coordinate.longitude];
    point = CGPointMake(point.x - _uiMapScrollView.frame.size.width / 2, point.y - _uiMapScrollView.frame.size.height / 2);
    
    point = [self pointInScrollViewRange:point];
    [_uiMapScrollView setContentOffset:point];
}

- (void)updateUserMarker
{
    CLLocationCoordinate2D coordinate = [GPSManager shared].currentLocation.coordinate;
    CGPoint point = [self pointFromLat:coordinate.latitude lon:coordinate.longitude];
    
    CGRect frame = _user.frame;
    frame.origin = CGPointMake(point.x - USER_MARKER_WIDTH / 2, point.y - USER_MARKER_HEIGHT / 2);
    _user.frame = frame;
}

- (void)updateMarkers
{
    NSMutableArray *places = [[AMODataManager shared] placesForCategoryId:_categoryId];
    
    // usunięcie starych markerów
    for (UIView *v in _uiMapScrollView.subviews) {
        if ([v isKindOfClass:[UIImageView class]]) {
            [v removeFromSuperview];
        }
    }
    
    // marker usera
    [_uiMapScrollView addSubview:_user];
    [self updateUserMarker];
    
    // dodanie nowych markerów
    for (NSDictionary *place in places) {
        
        double lat = [[place objectForKey:kLat] doubleValue];
        double lon = [[place objectForKey:kLon] doubleValue];
        
        CGPoint point = [self pointFromLat:lat lon:lon];
        
        MapMarkerView *v;
        Boolean selected = false;
        if([place isEqualToDictionary:_place]) {
            selected = true;
        }
        
         v= [[MapMarkerView alloc] initWithPlace:place category:_categoryId atPoint:point isSelected:selected];
        
        v.delegate = self;
        [_uiMapScrollView addSubview:v];
        [v release];
    }
}

- (void)mapMarkerWantToShowInfoViewOfPlace:(NSDictionary *)place
{
    //PlaceInfoViewController *vc = [[PlaceInfoViewController alloc] initWithPlace:place category:_categoryId];
    
    if([AppDelegate getIsPad])
    {
        PlaceInfoViewController_iPad *vc = [[PlaceInfoViewController_iPad alloc] initWithPlace:place category:_categoryId];
        [vc.navigationItem setLeftBarButtonItem:self.navigationItem.leftBarButtonItem];
        
        
        UISplitViewController *svc = [AppDelegate getSVC];
        
        //        
        NSMutableArray *details = [[NSMutableArray alloc] initWithArray:[[svc.viewControllers objectAtIndex:1] viewControllers]];
        [details removeLastObject];
        [details addObject:vc];
        svc.delegate = vc;
        [[svc.viewControllers objectAtIndex:1] setViewControllers:details];
        [details release];
        //
        
        //NSMutableArray *arr = [[NSMutableArray alloc] initWithArray:svc.viewControllers];
        //[arr replaceObjectAtIndex:1 withObject:vc];
        //svc.viewControllers = arr;
        //[arr release];
    }
    else
    {
        PlaceInfoViewController_iPhone *vc = [[PlaceInfoViewController_iPhone alloc] initWithPlace:place category:_categoryId];
        [self.navigationController pushViewController:vc animated:YES];
        [vc release];
    }
}

- (void)gpsManagerDidUpdateToLocation:(CLLocation *)location
{
    [self updateUserMarker];
}

- (BOOL) shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation {
    return YES;
}


@synthesize popover;
- (void)splitViewController:(UISplitViewController *)svc willHideViewController:(UIViewController *)aViewController withBarButtonItem:(UIBarButtonItem *)barButtonItem forPopoverController:(UIPopoverController *)pc
{
   // popover = pc;
    barButtonItem.title = @"Menu";
    //[self.navigationController setNavigationBarHidden:NO animated:YES];
    [self.navigationItem setLeftBarButtonItem:barButtonItem animated:YES];
}
- (void)splitViewController:(UISplitViewController *)svc willShowViewController:(UIViewController *)aViewController invalidatingBarButtonItem:(UIBarButtonItem *)barButtonItem
{
    [self.navigationItem setLeftBarButtonItem:nil animated:YES];
   // [self.navigationController setNavigationBarHidden:YES animated:YES];
   // [popover release];
}

-(void) viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    self.navigationItem.hidesBackButton = NO;
}

@end
