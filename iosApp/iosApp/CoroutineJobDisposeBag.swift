//
//  CoroutineJobDisposeBag.swift
//  iosApp
//
//  Created by François Dabonot on 16/07/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import Combine
import Shared

/// Store in a collection the kotlin coroutine jobs for canceling them.
/// Like the `Set<Task<(), Never>>()` of a task or `Set<AnyCancellable>` of combine
///
/// On `deinit` all stored jobs are cancelled
class CoroutineJobDisposeBag {
    private var storage = [CoroutinesCoreJob]()

    func append(job: CoroutinesCoreJob) {
        storage.append(job)
    }

    /// cancel and purge stored coroutine jobs
    func dispose() {
        storage.forEach {
            $0.cancel(cause: nil)
        }
        storage.removeAll()
    }

    deinit {
        dispose()
    }
}

extension CoroutinesCoreJob {
    /// same method as `store` of `combine` framework
    func store(in set: inout CoroutineJobDisposeBag) {
        set.append(job: self)
    }
}
