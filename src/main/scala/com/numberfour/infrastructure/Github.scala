package com.numberfour.infrastructure

import org.eclipse.egit.github.core._
import org.eclipse.egit.github.core.client._
import org.eclipse.egit.github.core.service._

trait Github {

  val githubToken = "94a2508e594e6cc7e2451f3168d412e88ada7bf0"
  val repoService = new RepositoryService
  repoService.getClient().setOAuth2Token(githubToken)

}