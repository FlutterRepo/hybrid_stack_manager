//
//  XRootController.m
//  Runner
//
//  Created by KyleWong on 2018/8/13.
//  Copyright © 2018 The Chromium Authors. All rights reserved.
//

#import "XRootController.h"
#import <hybrid_stack_manager/XURLRouter.h>

@interface XRootController ()

@end

@implementation XRootController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"Native根页面";
    // Do any additional setup after loading the view.
}

- (void)loadView{
    UIView *view = [[UIView alloc] initWithFrame:[UIScreen mainScreen].bounds];
    [view setBackgroundColor:[UIColor whiteColor]];
    UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 200, 40)];
    [btn setTitle:@"点击跳转Flutter" forState:UIControlStateNormal];
    [view addSubview:btn];
    [btn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [btn setCenter:view.center];
    [btn addTarget:self action:@selector(onJumpFlutterPressed) forControlEvents:UIControlEventTouchUpInside];
    self.view = view;
}

- (void)onJumpFlutterPressed{
    XOpenURLWithQueryAndParams(@"hrd://fdemo", @{@"flutter":@(true)}, nil);
}
@end
