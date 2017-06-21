//
//  PlacesViewController.m
//  AlternatywnaMapaOpola
//
//  Created by Grzegorz Frydrych on 23.05.2013.
//  Copyright (c) 2013 Binartech. All rights reserved.
//

#import "PlacesViewController.h"
#import "MapViewController.h"
#import "PlaceCell.h"
#import "AMODataManager.h"
#import "AppDelegate.h"

 @interface PlacesViewController ()

@end

@implementation PlacesViewController


- (id)initWithCategoryId:(int)categoryId
{
    self = [super initWithStyle:UITableViewStylePlain];
    if (self) {
        _categoryId = categoryId;
        _places = [[[AMODataManager shared] placesForCategoryId:_categoryId] retain];
        //_startid = [_places objectAtIndex:0]
    }
    
    return self;
}

- (void)dealloc
{
    [_places release];
    [super dealloc];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.title = [[AMODataManager shared].categories objectAtIndex:_categoryId];
    self.tableView.backgroundColor = [AMODataManager shared].colorListBackground;
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
    return _places.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentifier = @"PlaceCell";
    PlaceCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    
    if (cell == nil) {
        cell = [[[NSBundle mainBundle] loadNibNamed:cellIdentifier owner:nil options:nil] objectAtIndex:0];
    }
    
    //
    NSDictionary *data = [_places objectAtIndex:indexPath.row];
    [cell fillWithData:data row:indexPath.row];
    
    return cell;
}

- (float)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary *data = [_places objectAtIndex:indexPath.row];
    return [PlaceCell cellHeightForData:data];
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    //Zapis ostatnio klikniętego miejsca:
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    [prefs setInteger:indexPath.row forKey:@"lastPoi"];
    [prefs setInteger:_categoryId forKey:@"lastCategory"];
    [prefs synchronize];
    
    MapViewController *mvc = [[MapViewController alloc] initWithPlace:[_places objectAtIndex:indexPath.row] category:_categoryId];
    
    if([AppDelegate getIsPad])
    {
        UISplitViewController *svc = [AppDelegate getSVC];
        NSMutableArray *maps = [[NSMutableArray alloc] initWithArray:[[svc.viewControllers objectAtIndex:1] viewControllers]];
        UIBarButtonItem *but = [[maps lastObject] navigationItem].leftBarButtonItem;
        [mvc.navigationItem setLeftBarButtonItem:but];
        [maps removeLastObject];
        [maps addObject:mvc];
        svc.delegate = mvc;
        [[svc.viewControllers objectAtIndex:1] setViewControllers:maps];
        [maps release];
    }
    else
    {
        [self.navigationController pushViewController:mvc animated:YES];
        [mvc release];
    }
}

@end
