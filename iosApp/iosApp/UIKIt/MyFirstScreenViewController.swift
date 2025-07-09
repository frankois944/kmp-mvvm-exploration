//
//  MyFirstScreenViewController.swift
//  iosApp
//
//  Created by Francois Dabonot on 14/07/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import UIKit
import Combine
@preconcurrency import Shared

class MyFirstScreenViewController: UIViewController {

    // MARK: IBOutlet

    @IBOutlet var loadingIndicator: UIActivityIndicatorView!
    @IBOutlet var errorLabel: UILabel!
    @IBOutlet var userNaneLabel: UILabel!
    @IBOutlet var userIdLabel: UILabel!
    @IBOutlet var errorView: UIView!
    @IBOutlet var successView: UIView!
    @IBOutlet var tableView: UITableView!

    // MARK: - Properties

    private let viewModel: SharedViewModel<MainScreenViewModel> = .init(koinGet(parameters: ["VMWithUIKit"]))
    private let logger: KermitLogger = koinGet(parameters: ["MyFirstScreenViewController"])
    private var disposebag = Set<AnyCancellable>()
    private var jobDisposeBag = CoroutineJobDisposeBag()
    private var dataList: [FruitData]?
    var param1: String?
    var onNextView: (() -> Void)?

    // MARK: - Init

    override func viewDidLoad() {
        super.viewDidLoad()
        logger.d(messageString: "viewDidLoad")
        bindView()
    }

    // bind the viewmodel to the view
    private func bindView() {
        viewModel.instance.mainScreenUIState.toPublisher()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                self?.errorView.isHidden = true
                self?.successView.isHidden = true
                self?.loadingIndicator.isHidden = true
                switch onEnum(of: $0) {
                case .error(let error):
                    self?.errorView.isHidden = false
                    self?.errorLabel.text = "ERROR : \(error.message)"
                case .loading:
                    self?.loadingIndicator.isHidden = false
                case .success(let value):
                    self?.successView.isHidden = false
                    self?.userNaneLabel.text = value.profile.username
                }
            }
            .store(in: &disposebag)
        viewModel.instance.datasource.toPublisher()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                self?.dataList = $0
                self?.tableView.reloadData()
            }
            .store(in: &disposebag)
        viewModel.instance.userId.toPublisher()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                self?.userIdLabel.text = "BONJOUR: \($0 ?? "NULL")"
            }
            .store(in: &disposebag)
    }

    // MARK: - IBAction

    @IBAction func updateUserId() {
        logger.d(messageString: "updateUserId")
        viewModel.instance.updateUserId()
    }

    @IBAction func addAll() {
        logger.d(messageString: "add new fruit")
        viewModel.instance.addRandomFruitToDatabase()
    }

    @IBAction func removeAll() {
        logger.d(messageString: "remove all fruits")
        viewModel.instance.removeAllFruitFromDatabase()
    }

    @IBAction func retry() {
        logger.d(messageString: "retry")
        viewModel.instance
            .reload()
            .store(in: &jobDisposeBag)
    }

    deinit {
        logger.d(messageString: "DEINIT")
    }

}

// MARK: - UITableViewDelegate

extension MyFirstScreenViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.onNextView?()
    }
}

// MARK: - UITableViewDataSource

extension MyFirstScreenViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dataList?.count ?? 0
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = UITableViewCell(style: .default, reuseIdentifier: "Cell")
        cell.textLabel?.text = dataList?[indexPath.row].fullName ?? "N/A"
        return cell
    }

}
