import UIKit
import Stripe

private var COMMAND = CDVInvokedUrlCommand.init()

@objc(StripePlugin) class StripePlugin : CDVPlugin,  STPAddCardViewControllerDelegate {
	@objc(payments_activity:)
	func payments_activity(_ command: CDVInvokedUrlCommand) {
		COMMAND = command 
	    let key = command.arguments[4] as? String ?? ""

	    STPPaymentConfiguration.shared().publishableKey = key

        let addCardViewController = STPAddCardViewController()
        addCardViewController.delegate = self
        
        // Present add card view controller
        let navigationController = UINavigationController(rootViewController: addCardViewController)
        let appDelegate = UIApplication.shared.delegate as! CDVAppDelegate
        appDelegate.window.rootViewController = navigationController
	    
	}

	func addCardViewControllerDidCancel(_ addCardViewController: STPAddCardViewController) {
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "User cancelled")
        self.commandDelegate!.send(pluginResult, callbackId: COMMAND.callbackId)
        let appDelegate = UIApplication.shared.delegate as! CDVAppDelegate
        appDelegate.window.rootViewController = self.viewController
    }

    func addCardViewController(_ addCardViewController: STPAddCardViewController, didCreateToken token: STPToken, completion: @escaping STPErrorBlock) {
    
    	let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: token.tokenId)
    	self.commandDelegate!.send(pluginResult, callbackId: COMMAND.callbackId)
    	let appDelegate = UIApplication.shared.delegate as! CDVAppDelegate
        appDelegate.window.rootViewController = self.viewController
	}
}
