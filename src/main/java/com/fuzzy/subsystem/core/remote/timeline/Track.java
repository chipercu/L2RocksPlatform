package com.fuzzy.subsystem.core.remote.timeline;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;

import java.time.LocalDateTime;
import java.util.Objects;

public class Track implements RemoteObject {

	private final LocalDateTime begin;
	private final LocalDateTime end;
	private final long typeId;

	public Track(LocalDateTime begin, LocalDateTime end, long typeId) {
		this.begin = begin;
		this.end = end;
		this.typeId = typeId;
	}

	public LocalDateTime getBegin() {
		return begin;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public long getType() {
		return typeId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Track track = (Track) o;
		return Objects.equals(begin, track.begin) &&
				Objects.equals(end, track.end) &&
				Objects.equals(typeId, track.typeId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(begin, end, typeId);
	}

	@Override
	public String toString() {
		return "Track{" +
				"begin=" + begin +
				", end=" + end +
				", type=" + typeId +
				'}';
	}
}
