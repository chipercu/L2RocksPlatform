package com.fuzzy.subsystem.core.remote.timeline;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class Timeline implements RemoteObject {

	private final long employeeId;
	private final LocalDate date;
	private final ArrayList<Track> tracks;

	public Timeline(long employeeId, LocalDate date, ArrayList<Track> tracks) {
		this.employeeId = employeeId;
		this.date = date;
		this.tracks = tracks;
	}

	public long getEmployeeId() {
		return employeeId;
	}

	public LocalDate getDate() {
		return date;
	}

	public ArrayList<Track> getTracks() {
		return tracks;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Timeline timeline = (Timeline) o;
		return employeeId == timeline.employeeId &&
				Objects.equals(date, timeline.date) &&
				Objects.equals(tracks, timeline.tracks);
	}

	@Override
	public int hashCode() {
		return Objects.hash(employeeId, date, tracks);
	}

	@Override
	public String toString() {
		return "Timeline{" +
				"employeeId=" + employeeId +
				", date=" + date +
				", tracks=" + tracks +
				'}';
	}
}
