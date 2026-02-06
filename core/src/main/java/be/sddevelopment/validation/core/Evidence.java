package be.sddevelopment.validation.core;

import static java.util.Optional.ofNullable;

import java.util.HashMap;
import java.util.Optional;

public record Evidence (
		HashMap<String, String> evidence
) {

	public Evidence {
		if (evidence == null) {
			throw new IllegalArgumentException("Evidence must not be null");
		}
	}

	public Evidence put(String key, String value) {
		evidence.put(key, value);
		return this;
	}

	public Optional<String> get(String key) {
		return ofNullable(evidence.get(key));
	}

	public boolean isEmpty() {
		return evidence.isEmpty();
	}

	public static Evidence empty() {
		return new Evidence(new HashMap<>());
	}

	public Evidence merge(Evidence evidence) {
		if (evidence == null) {
			return this;
		} else {
			var mergedEvidence = new HashMap<>(this.evidence);
			mergedEvidence.forEach((key, value) -> mergedEvidence.merge(key, value + evidence.evidence.get(key), String::concat));
			return new Evidence(mergedEvidence);
		}
	}
}
