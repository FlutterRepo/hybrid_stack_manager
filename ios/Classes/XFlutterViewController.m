//
//  XFlutterViewController.m
//  flutter_chann_plugin
//
//  Created by 正物 on 18/03/2018.
//

#import "XFlutterViewController.h"

@interface XFlutterViewController ()
@property (nonatomic,assign) BOOL enableViewWillAppear;
@end

@implementation XFlutterViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.enableViewWillAppear = TRUE;
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillAppear:(BOOL)animated{
    if(self.enableViewWillAppear == FALSE)
        return;
    [super viewWillAppear:animated];
    self.enableViewWillAppear = FALSE;
}

- (void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated{
    [super viewDidDisappear:animated];
    self.enableViewWillAppear = TRUE;
}
/*
 #pragma mark - Navigation
 
 // In a storyboard-based application, you will often want to do a little preparation before navigation
 - (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
 // Get the new view controller using [segue destinationViewController].
 // Pass the selected object to the new view controller.
 }
 */
//- (UIEdgeInsets)paddingEdgeInsets{
//    UIEdgeInsets edgeInsets = UIEdgeInsetsZero;
//    if (@available(iOS 11, *)) {
//        edgeInsets = UIEdgeInsetsMake(0, self.view.safeAreaInsets.left, self.view.safeAreaInsets.bottom, self.view.safeAreaInsets.right);
//    } else {
//        edgeInsets = UIEdgeInsetsZero;
//    }
//    return edgeInsets;
//}
@end

