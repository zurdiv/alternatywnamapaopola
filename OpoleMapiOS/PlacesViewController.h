//
//  PlacesViewController.h
//  AlternatywnaMapaOpola
//
//  Created by Grzegorz Frydrych on 23.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MapViewController.h"

@interface PlacesViewController : UITableViewController
{
    int _categoryId;
    int _startId;
    NSArray *_places;
}

- (id)initWithCategoryId:(int)categoryId;
@end
