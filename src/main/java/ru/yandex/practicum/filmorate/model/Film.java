package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Film{
	private Long duration;
	private LocalDate releaseDate;
	private Long rate;
	private HashSet<Genre> genres;
	private String name;
	private String description;
	private Long id;
	private Mpa mpa;
}