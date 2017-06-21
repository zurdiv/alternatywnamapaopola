//
//  PlaceCell.h
//  AlternatywnaMapaOpola
//
//  Created by Grzegorz Frydrych on 24.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface PlaceCell : UITableViewCell

@property (retain, nonatomic) IBOutlet UILabel *uiNumber;
@property (retain, nonatomic) IBOutlet UILabel *uiText;

- (void)fillWithData:(NSDictionary *)data row:(int)row;
+ (int)cellHeightForData:(NSDictionary *)data;

@end
