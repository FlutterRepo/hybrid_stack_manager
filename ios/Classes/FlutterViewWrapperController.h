//
//  FlutterViewWrapperController.h
//  Runner
//
//  Created by 正物 on 08/03/2018.
//  Copyright © 2018 The Chromium Authors. All rights reserved.
//

#import <Flutter/Flutter.h>
#import "XFlutterViewController.h"
#import "UIViewController+URLRouter.h"

typedef void (^FlutterViewWillAppearBlock) (void);

@interface FlutterViewWrapperController : UIViewController
+ (XFlutterViewController *)flutterVC;
@property(nonatomic,copy) NSString *curFlutterRouteName;
@property(nonatomic,copy) FlutterViewWillAppearBlock viewWillAppearBlock;
@end
