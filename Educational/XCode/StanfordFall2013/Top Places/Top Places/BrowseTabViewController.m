//
//  BrowseTabViewController.m
//  Top Places
//
//  Created by Ethan Petuchowski on 12/30/13.
//  Copyright (c) 2013 Ethan Petuchowski. All rights reserved.
//

#import "BrowseTabViewController.h"
#import "TopPlacesObject.h"

@interface BrowseTabViewController ()

@property (nonatomic) TopPlacesObject *topPlacesObject;
@end

@implementation BrowseTabViewController

- (TopPlacesObject *)topPlacesObject
{
    if (!_topPlacesObject) _topPlacesObject = [[TopPlacesObject alloc] init];
    return _topPlacesObject;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self numberOfSectionsInTableView:nil];
}


#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [[self.topPlacesObject alphabeticalArrayOfCountries] count];
}

- (NSString *)sectionNameOfNum:(NSInteger)sectionNum
{
    return self.topPlacesObject.alphabeticalArrayOfCountries[sectionNum];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSString *countryName = [self sectionNameOfNum:section];
    return [[self.topPlacesObject countryArrayForCountry:countryName] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    
    // Configure the cell...
    
    return cell;
}

/*
#pragma mark - Navigation

// In a story board-based application,
// you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}

 */

@end