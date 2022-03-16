package org.georgemalandrakis.archion;

import com.codahale.metrics.health.HealthCheck;

public class ArchionHealthCheck extends HealthCheck {
	public ArchionHealthCheck() {

	}

	@Override
	protected Result check() throws Exception {
		return Result.healthy();
	}
}
