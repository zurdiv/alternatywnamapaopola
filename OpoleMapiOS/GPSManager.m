//
//  GPSManager.m
//  AlternatywnaMapaOpola
//
//  Created by Grzegorz Frydrych on 27.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import "GPSManager.h"


@implementation GPSManager

#pragma mark - Singleton

+ (GPSManager *)shared
{
    static GPSManager *instance;
	static dispatch_once_t onceToken;
	dispatch_once(&onceToken, ^{
		instance = [[GPSManager alloc] init];
	});
	
	return instance;
}

- (id)copyWithZone:(NSZone *)zone
{
    return self;
}

- (void)dealloc
{
    [_locationManager stopUpdatingLocation];
    [_locationManager release];
    
    self.previousLocation = nil;
    self.currentLocation = nil;
    
    [super dealloc];
}

#pragma mark

- (id)init
{
    self = [super init];
    if (self) {
        _locationManager = [[CLLocationManager alloc] init];
        _locationManager.delegate = self;
        _locationManager.desiredAccuracy = kCLLocationAccuracyBest;
        
//#warning FIX
//        CLLocation *location = [[CLLocation alloc] initWithLatitude:50.66965 longitude:17.91987];
//        self.currentLocation = location;
    }
    
    return self;
}

- (void)startUpdating
{
    [_locationManager startUpdatingLocation];
}

- (void)stopUpdating
{
    [_locationManager stopUpdatingLocation];
}

#pragma mark - CLLocationManagerDelegate

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error
{
    self.currentLocation = nil;
}

- (void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation
{
    self.previousLocation = _currentLocation;
    self.currentLocation = newLocation;
    
    if (_delegate) {
        [_delegate gpsManagerDidUpdateToLocation:newLocation];
    }
}

@end
