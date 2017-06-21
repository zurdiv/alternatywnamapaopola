//
//  CategoryCell.m
//  AlternatywnaMapaOpola
//
//  Created by Grzegorz Frydrych on 24.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import "CategoryCell.h"
#import "AMODataManager.h"


@implementation CategoryCell

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
    self.backgroundColor = [AMODataManager shared].colorListBackground;
    
    UIView *view = [[UIView alloc] initWithFrame:CGRectZero];
    view.backgroundColor = [AMODataManager shared].colorListSelected;
    self.selectedBackgroundView = view;
    [view release];
}

- (void)dealloc
{
    [_uiImage release];
    [_uiName release];
    [super dealloc];
}

@end