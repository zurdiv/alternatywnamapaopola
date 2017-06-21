//
//  DataManager.m
//
//  Created by Grzegorz Frydrych on 23.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import "AMODataManager.h"
#import "LanguageManager.h"
#import <CoreLocation/CoreLocation.h>
#import <MapKit/MapKit.h>


@implementation AMODataManager

#pragma mark - Singleton

+ (AMODataManager *)shared
{
    static AMODataManager *instance;
	static dispatch_once_t onceToken;
	dispatch_once(&onceToken, ^{
		instance = [[AMODataManager alloc] init];
	});
	
	return instance;
}

- (id)copyWithZone:(NSZone *)zone
{
    return self;
}

- (void)dealloc
{
    [_categories release];
    [_places release];
    [_colorListBackground release];
    [_colorListSelected release];
    [_colorActionBar release];
    [super dealloc];
}

#pragma mark

- (id)init
{
    self = [super init];
    if (self) {
        
        // sprawdzenie wersji językowej
        NSString *lang = [LanguageManager systemLanguage];
        
        if ([LanguageManager isSupportedLanguage:lang] == NO) {
            lang = kLMEnglish;
        }

        NSString *filename = [NSString stringWithFormat:@"poi_%@", lang];
        
        // wczytanie danych POI
        NSString *path = [[NSBundle mainBundle] pathForResource:filename ofType:@"plist"];
        _places = [[NSArray alloc] initWithContentsOfFile:path];
        
        // utworzenie listy kategorii
        _categories = [[NSMutableArray alloc] init];
        NSString *lastCategory = @"";
        
        for (NSDictionary *place in _places) {
            NSString *name = [place objectForKey:kCategory];
            if ([name isEqualToString:lastCategory] == NO) {
                lastCategory = name;
                [_categories addObject:name];
            }
        }
        [_categories addObject:NSLocalizedString(@"Category_all",@"All categories")];
        
        // sprawdzenie czy lokalizacja mieści się na mapie
        MKMapPoint topLeft = MKMapPointForCoordinate(CLLocationCoordinate2DMake(V_TOP, V_LEFT));
        MKMapPoint bottomRight = MKMapPointForCoordinate(CLLocationCoordinate2DMake(V_BOTTOM, V_RIGHT));
        MKMapRect mapRect = MKMapRectMake(topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y);
        
        for (NSMutableDictionary *place in _places) {
            
            double lat = [[place objectForKey:kLat] doubleValue];
            double lon = [[place objectForKey:kLon] doubleValue];
            CLLocationCoordinate2D targetCoordinate = CLLocationCoordinate2DMake(lat, lon);
            BOOL isInside = MKMapRectContainsPoint(mapRect, MKMapPointForCoordinate(targetCoordinate));
            
            if (isInside == NO) {
                NSLog(@"%d %@", [_places indexOfObject:place] + 1, [place objectForKey:kName]);
            }
        }
        
        // kolory wcześniejsze
        //self.colorListBackground = [UIColor colorWithRed:163.0/255 green:140.0/255 blue:100.0/255 alpha:1.0];
        //self.colorListSelected = [UIColor colorWithRed:185.0/255 green:158.0/255 blue:113.0/255 alpha:1.0];
        //Kolory aktualne (jak tło splash screena)
        self.colorListBackground = [UIColor colorWithRed:200.0/255 green:66.0/255 blue:42.0/255 alpha:1.0];
        self.colorActionBar = [UIColor colorWithRed:61.0/255 green:118.0/255 blue:121.0/255 alpha:1.0];
        self.colorListSelected = [UIColor colorWithRed:185.0/255 green:158.0/255 blue:113.0/255 alpha:1.0];
    
    }
    
    return self;
}

- (NSString *)nameOfCategoryId:(int)categoryId
{
    return [_categories objectAtIndex:categoryId];
}

- (NSDictionary *)placeId:(int)placeId
{
    return [_places objectAtIndex:placeId];
}

- (NSMutableArray *)placesForCategoryId:(int)categoryId
{
    NSString *categoryName = [_categories objectAtIndex:categoryId];
    NSMutableArray *categoryPlaces = [[NSMutableArray alloc] init];
    int i=1;
    for (NSDictionary *place in _places) {
        if ([[place objectForKey:kCategory] isEqualToString:categoryName] || [categoryName isEqualToString:NSLocalizedString(@"Category_all",@"All categories")]) {
            NSMutableDictionary *newPlace = [[NSMutableDictionary alloc] initWithDictionary:place copyItems:YES];
            [newPlace setObject:[NSString stringWithFormat:@"%d",i++] forKey:kLp];
            [categoryPlaces addObject:newPlace];
        }
    }
    
    return [categoryPlaces autorelease];
}

#pragma mark

+ (void)showAlertWithTitle:(NSString *)title message:(NSString *)message delegate:(id)delegate
{
    UIAlertView *av = [[UIAlertView alloc] initWithTitle:title
                                                 message:message
                                                delegate:delegate
                                       cancelButtonTitle:@"Ok"
                                       otherButtonTitles:nil];
    [av show];
}

@end
