//
//  GPSManager.h
//  AlternatywnaMapaOpola
//
//  Created by Grzegorz Frydrych on 27.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>


@protocol GPSManagerDelegate <NSObject>
- (void)gpsManagerDidUpdateToLocation:(CLLocation *)location;
@end


@interface GPSManager : NSObject <CLLocationManagerDelegate>
{
    CLLocationManager *_locationManager;
}

@property (nonatomic, assign) id <GPSManagerDelegate> delegate;
@property (nonatomic, retain) CLLocation *previousLocation;
@property (nonatomic, retain) CLLocation *currentLocation;

+ (GPSManager *)shared;
- (void)startUpdating;
- (void)stopUpdating;

@end
