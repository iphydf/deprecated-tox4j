package im.tox.hlapi.adapter.performer

import im.tox.hlapi.state.CoreState.ToxState

object DiskIOActionPerformer {
  def perform(state: ToxState): ToxState = state
}
