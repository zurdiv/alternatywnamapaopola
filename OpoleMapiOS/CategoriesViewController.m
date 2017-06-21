//
//  CategoriesViewController.m
//  AlternatywnaMapaOpola
//
//  Created by Grzegorz Frydrych on 23.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import "CategoriesViewController.h"
#import "PlacesViewController.h"
#import "CategoryCell.h"
#import "AMODataManager.h"
#import "AppDelegate.h"

@interface CategoriesViewController ()

@end

@implementation CategoriesViewController


- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.title = NSLocalizedString(@"PlaceCategory", nil);
    self.tableView.backgroundColor = [AMODataManager shared].colorListBackground;
    self.navigationController.navigationBar.tintColor = [AMODataManager shared].colorActionBar;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [AMODataManager shared].categories.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentifier = @"CategoryCell";
    CategoryCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    
    if (cell == nil) {
        cell = [[[NSBundle mainBundle] loadNibNamed:cellIdentifier owner:nil options:nil] objectAtIndex:0];
    }
    
    //
    cell.uiName.text = [[[AMODataManager shared].categories objectAtIndex:indexPath.row] uppercaseString];
    cell.uiName.font = [UIFont fontWithName:FONT_DEFTONE size:24];
    
    return cell;
}

- (float)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60;
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view
    
    PlacesViewController *vc = [[PlacesViewController alloc] initWithCategoryId:indexPath.row];
    [self.navigationController pushViewController:vc animated:YES];
    //[self.navigationController splitViewController NSString]
    [vc release];
}

- (BOOL) shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation {
    return YES;
}



@end
