//
//  cMasterViewController.h
//  11
//
//  Created by Ethan Petuchowski on 11/7/13.
//  Copyright (c) 2013 Ethan Petuchowski. All rights reserved.
//

#import <UIKit/UIKit.h>

@class cDetailViewController;

@interface cMasterViewController : UITableViewController

@property (strong, nonatomic) cDetailViewController *detailViewController;

@end
