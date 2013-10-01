package com.numberfour.infrastructure

import org.eclipse.egit.github.core._
import org.eclipse.egit.github.core.client._
import org.eclipse.egit.github.core.service._
import com.typesafe.config.ConfigFactory
import com.numberfour.Configurable

trait Github extends Configurable {

  val githubToken = config.getString("numberfour.github.token")

  val repoService = new RepositoryService
  repoService.getClient().setOAuth2Token(githubToken)

}