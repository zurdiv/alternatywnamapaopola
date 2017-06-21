//
//  DataManager.h
//
//  Created by Grzegorz Frydrych on 23.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import <Foundation/Foundation.h>

#define FONT_LIBEL @"LibelSuit-Regular"
#define FONT_DEFTONE @"DeftoneStylus-Regular"

#define kLp @"Lp"
#define kCategory @"Category"
#define kName @"Title"
#define kDescription @"Description"
#define kLat @"Latitude"
#define kLon @"Longitude"
#define kFeature @"Feature"

#define MAP_WIDTH   4016
#define MAP_HEIGHT  4016

#define V_LEFT      17.9093041441
#define V_RIGHT     17.9415344207
#define V_TOP       50.6760705494
#define V_BOTTOM	50.6562561316


@interface AMODataManager : NSObject

@property (nonatomic, retain) NSMutableArray *categories;
@property (nonatomic, retain) NSArray *places;
@property (nonatomic, retain) UIColor *colorListBackground;
@property (nonatomic, retain) UIColor *colorActionBar;
@property (nonatomic, retain) UIColor *colorListSelected;

+ (AMODataManager *)shared;
+ (void)showAlertWithTitle:(NSString *)title message:(NSString *)message delegate:(id)delegate;

- (NSMutableArray *)placesForCategoryId:(int)categoryId;

@end
