(function ($, window)
{

	window.BurningSeries = {
		baseUrl: 'http://bs.to/serie/',
		baseApiUrl: 'http://bs.to/api/',
		coverUrl: '//s.bs.to/img/cover/{id}.jpg',
		sessionId: null,

		SORT_ALPHABETICAL: 0,
		SORT_GENRE: 1,
		SORT_NEWEST: 2,

		ANDROID: 'android',

		calls: 0,
		postCalls: 0,

		/*****************************************
		 ************ General Helpers ************
		 *****************************************/

		search: function (name, exact, callback)
		{
			if (typeof callback == 'undefined') {
				callback = exact;
				exact = false;
			}

			this.getSeries(function (data)
			{
				var result = [];

				for (var key in data) {
					if (!exact && data[key].series.toLowerCase().indexOf(name.toLowerCase()) != -1) {
						result.push(data[key]);
					} else {
						if (exact && data[key].series.toLowerCase() == name.toLowerCase()) {
							result.push(data[key]);
						}
					}
				}

				if (exact && result.length == 1) {
					result = result[0];
				} else {
					if (exact && result.length > 1) {
						result = {};
					}
				}

				callback(result);
			});
		},

		getByName: function (name, callback)
		{
			this.search(name, true, function (data)
			{
				if (data.length == 0) {
					callback({});
					return;
				}

				BurningSeries.getSerie(data.id, callback);
			});
		},

		buildSerieUrl: function (serie, callback)
		{
			this.getSerieObject(serie, function (data)
			{
				if (typeof data == 'undefined' || data.length == 0) {
					callback('');
					return;
				}

				callback(BurningSeries.buildLink(data.url));
			});
		},

		buildSeasonUrl: function (serie, season, callback)
		{
			season = parseInt(season);

			this.buildSerieUrl(serie, function (data)
			{
				callback(data + '/' + season);
			});
		},

		buildEpisodeUrl: function (serie, season, episode, callback)
		{
			episode = parseInt(episode);

			this.buildSeasonUrl(serie, season, function (data)
			{
				callback(data + '/' + episode + '-Episode');
			});
		},

		buildUrl: function (serie, season, episode, callback)
		{
			if (typeof callback != 'undefined') {
				this.buildEpisodeUrl(serie, season, episode, callback);
			} else {
				if (typeof  season != 'undefined') {
					this.buildSeasonUrl(serie, season, episode);
				} else {
					this.buildEpisodeUrl(serie, season);
				}
			}
		},

		getCover: function(serie, callback)
		{
			this.getSerieObject(serie, function(data) {
				callback(BurningSeries.coverUrl.replace('{id}', data.id));
			});
		},

		getGenres: function(callback)
		{
			this.getSeries(function(data) {
				var keys = [];
				for(var key in data) {
					keys.push(key);
				}
				callback(keys);
			}, this.SORT_GENRE);
		},

		hasWatched: function(serie, season, episode, callback)
		{
			if (this.sessionId == null) {
				callback(false);
			}

			this.getSerieObject(serie, function(serie) {
				BurningSeries.getSeason(serie.id, season, function(season) {
					episode = parseInt(episode);

					for(var key in season) {
						if(season[key]['epi'] != episode) {
							continue;
						}

						season[key]['watched'] == 1 ? callback(true) : callback(false);
						return;
					}

					callback(false);
				});
			});
		},

		getNextUnwatchedEpisode: function(serie, season, offset, callback)
		{
			alert('Not yet implemented');return;

			if(typeof callback == 'undefined') {
				if(typeof offset == 'undefined') {
					callback = season;
					season = undefined;
				} else {
					callback = offset;
					offset = undefined;
				}
			}

			this.getSerieObject(serie, function(serie) {
				var seasons = [];
				if(typeof season == 'undefined') {
					for (var i= 1; i<=serie.seasons; i++) {
						seasons.push(i);
					}
				} else {
					seasons = [parseInt(season)];
				}

				for(var key in seasons) {
					var season = seasons[key];
// Async call in for loop results in unwanted results (season 8 answers before season 7 etc)
					BurningSeries.getSeason(serie.id, season, function(episodes) {
						if(typeof offset != 'undefined') {
							episodes = episodes.slice(offset);
						}

						for(var i in episodes) {
							var episode = episodes[i];

							if(episode.watched == 1) {
								continue;
							}
console.log(episode.epi);
							BurningSeries.getEpisode(serie.id, season, episode.epi, callback);
							return;
						}
					});
				}

				callback({});
			});
		},

		getNextUnwatchedMovie: function(serie, callback)
		{
			this.getNextUnwatchedEpisode(serie, 0, callback);
		},

		markAsFavorite: function(serie, callback)
		{
			if (this.sessionId == null) {
				callback(false);
			}

			this.getSerieObject(serie, function(serie) {
				BurningSeries.getFavoriteSeries(function(favorites) {
					var favoriteIds = [];

					for(var key in favorites) {
						favoriteIds.push(favorites[key].id);
					}

					if($.inArray(serie.id, favoriteIds) != -1) {
						callback(true);
						return;
					}

					favoriteIds.push(serie.id);

					callback(BurningSeries.setFavoriteSeries(favoriteIds));
				});
			});
		},

		unmarkAsFavorite: function(serie, callback)
		{
			if (this.sessionId == null) {
				callback(false);
			}

			this.getSerieObject(serie, function(serie) {
				BurningSeries.getFavoriteSeries(function(favorites) {
					var favoriteIds = [];
					var isFavorite = false;

					for(var key in favorites) {
						if(favorites[key].id == serie.id) {
							isFavorite = true;
							continue;
						}

						favoriteIds.push(favorites[key].id);
					}

					if(!isFavorite) {
						callback(true);
						return;
					}

					callback(BurningSeries.setFavoriteSeries(favoriteIds));
				})
			});
		},

		isFavoritedSerie: function(serie, callback)
		{
			if (this.sessionId == null) {
				return false;
			}

			this.getSerieObject(serie, function(serie) {
				BurningSeries.getFavoriteSeries(function(favorites) {
					for(var key in favorites) {
						if(favorites[key].id == serie.id) {
							callback(true);
							return;
						}
					}

					callback(false);
				});
			});
		},

		/*************************************
		 ************ General API ************
		 *************************************/

		getSeries: function (callback, sort)
		{
			var link = 'series';
			if (sort == this.SORT_GENRE) {
				link = 'series:genre';
			}

			var nCallback = callback;
			if (sort == this.SORT_NEWEST) {
				nCallback = function (data)
				{
					data = BurningSeries.sortById(data);
					callback(data);
				}
			}

			this.call(link, nCallback);
		},

		getByGenre: function (genre, callback)
		{
			var series = this.getSeries(function (series)
			{
				if (parseInt(genre) == genre) {
					genre = parseInt(genre);
				}

				if (typeof genre == 'string') {
					if (series.hasOwnProperty(genre)) {
						callback(series[genre]);
						return;
					}

					callback([]);
					return;
				}
				else {
					if (Number.isInteger(genre)) {
						for (var key in series) {
							if (series[key].id == genre) {
								callback(series[key]);
								return;
							}
						}
					}
				}

				callback([]);
			}, this.SORT_GENRE);
		},

		getNewest: function (callback)
		{
			return this.getSeries(callback, this.SORT_NEWEST);
		},

		getSerie: function (serie, callback)
		{
			serie = parseInt(serie);

			this.call('series/' + serie + '/1', function (data)
			{
				if (typeof data.series == 'undefined') {
					callback({});
					return;
				}

				callback(data.series);
			});
		},

		getSeason: function (serie, season, callback)
		{
			serie = parseInt(serie);
			season = parseInt(season);

			this.call('series/' + serie + '/' + season, function (data)
			{
				if (typeof data.epi == 'undefined') {
					callback([]);
					return;
				}

				callback(data.epi);
			});
		},

		getMovies: function (serie, callback)
		{
			this.getSeason(serie, 0, callback);
		},

		getEpisode: function (serie, season, episode, callback)
		{
			serie = parseInt(serie);
			season = parseInt(season);
			episode = parseInt(episode);

			this.call('series/' + serie + '/' + season + '/' + episode, function (data)
			{
				if (typeof data.epi == 'undefined') {
					callback({});
					return;
				}

				callback(data.epi);
			});
		},

		get: function (serie, season, episode, callback)
		{
			// All parameters set
			if (typeof callback != 'undefined') {
				this.getEpisode(serie, season, episode, callback);
				// One parameter missing
			} else {
				if (typeof episode != 'undefined') {
					this.getSeason(serie, season, episode);
				}
				else {
					// Only serie is set
					this.getSerie(serie, season);
				}
			}
		},

		getHoster: function (serie, season, episode, callback)
		{
			serie = parseInt(serie);
			season = parseInt(season);
			episode = parseInt(episode);

			this.call('series/' + serie + '/' + season + '/' + episode, function (data)
			{
				if (typeof data.links == 'undefined') {
					callback([]);
					return;
				}

				callback(data.links);
			});
		},

		getVideo: function (id, callback)
		{
			id = parseInt(id);

			this.call('watch/' + id, function (data)
			{
				//BurningSeries.markAsUnwatched(data.epi);
				callback(data);
			});
		},

		markAsWatched: function (id, season, episode)
		{
			if (this.sessionId == null) {
				return false;
			}

			if (typeof season != 'undefined' && typeof episode != 'undefined') {
				this.getHoster(id, season, episode, function (data)
				{
					var randomHoster = data[Math.floor(Math.random() * data.length)];
					BurningSeries.markAsWatched(randomHoster.id);
				});
			}

			id = parseInt(id);

			this.call('watch/' + id);

			return true;
		},

		markAsUnwatched: function (id, season, episode, callback)
		{
			if (this.sessionId == null) {
				callback(false);
				return false;
			}

			if (typeof season != 'undefined' && typeof episode != 'undefined') {
				this.getEpisode(id, season, episode, function (data)
				{
					BurningSeries.markAsUnwatched(data.id, callback);
				});
				return;
			} else {
				callback = season;
			}

			if (typeof id.id != 'undefined') {
				id = id.id;
			}

			id = parseInt(id);

			this.call('unwatch/' + id, function (data)
			{
				callback(data.success);
			});
		},

		getFavoriteSeries: function (callback)
		{
			if (this.sessionId == null) {
				callback([]);
				return;
			}

			this.call('user/series', callback);
		},

		setFavoriteSeries: function (series)
		{
			if (this.sessionId == null) {
				return false;
			}

			series = series.join();

			if (series.length == 0) {
				series = 0;
			}

			this.call('user/series/set/' + series);

			return true;
		},

		login: function (name, password, callback)
		{
			if (typeof name != 'string' || typeof password != 'string') {
				alert('Name and Password need to be strings');
				return;
			}

			var login = {
				'login[user]': name,
				'login[pass]': password
			};

			this.call('login', function (data)
			{
				console.log(data);
				BurningSeries.setSessionId(data.session);
				callback(data.session);
			}, login);
		},

		logout: function ()
		{
			this.call('logout');
			this.setSessionId();
		},

		getVersion: function (system, callback)
		{
			this.call('version/' + system, function (data)
			{
				if (typeof data.version == 'undefined') {
					callback(false);
					return;
				}

				callback(data.version);
			});
		},


		/****************************************
		 ************ Config Helpers ************
		 ****************************************/

		init: function (login, password)
		{
			if (typeof login != 'undefined') {
				// Login
				if (typeof password != 'undefined') {
					this.login(login, password);
				} // Already logged in
				else {
					this.setSessionId(login);
				}
			}
		},

		setBaseUrl: function (baseUrl)
		{
			if (baseUrl != '') {
				this.baseUrl = baseUrl;
			}
		},

		getBaseUrl: function ()
		{
			return this.baseUrl;
		},

		setApiFunction: function (baseApiUrl)
		{
			if (baseApiUrl != '') {
				this.baseApiUrl = baseApiUrl;
			}
		},

		getApiUrl: function ()
		{
			return this.baseApiUrl;
		},

		setCoverUrl: function (coverUrl)
		{
			if (coverUrl != '' && coverUrl.indexOf('{id}') != -1) {
				this.coverUrl = coverUrl;
			}
		},

		getCoverUrl: function ()
		{
			return this.coverUrl;
		},

		setSessionId: function (sessionId)
		{
			if (typeof sessionId == 'undefined') {
				sessionId = null;
			}

			this.sessionId = sessionId;
		},

		getSessionId: function ()
		{
			return this.sessionId;
		},

		/******************************************
		 ************ Internal Helpers ************
		 ******************************************/

		buildLink: function (link)
		{
			return this.baseUrl + link;
		},

		getSerieObject: function (serie, callback)
		{
			// It's an array which seems to be a series? Good
			if (typeof serie.id != 'undefined') {
				callback(serie);
				return;
			}

			// If it's an integer we'll assume that it's an ID
			if (Number.isInteger(serie)) {
				this.getSerie(serie, function (data)
				{
					if (data.length == 0) {
						alert('Error getting the series');
					}

					callback(data);
				});
				return;
			}

			// A string? Guess that's a title we need to search
			if (typeof serie == 'string') {
				this.search(serie, true, function (data)
				{
					if (data.length == 0) {
						// Probably it was an ID?
						if (parseInt(serie) == serie) {
							BurningSeries.getSerieObject(parseInt(serie), callback);
							return;
						}

						alert("Couldn't find a Serie with that name");
						return;
					}

					BurningSeries.getSerie(data.id, callback);
				});
				return;
			}

			// Still here?
			alert('Serie needs to be either an integer, a string or an array');
		},

		call: function (link, callback, post)
		{
			link = this.baseApiUrl + link;

			if (this.sessionId !== null) {
				link += '?s=' + this.sessionId;
			}

			this.calls++;

			httpMethod = 'post';
			if (typeof post == 'undefined') {
				httpMethod = 'get';
				post = [];
				this.postCalls++;
			}

			$.ajax({
				data: post,
				dataType: 'json',
				type: httpMethod,
				url: link,
				success: callback
			});
		},

		sortById: function (data)
		{
			var sortable = [];
			for (var key in data) {
				sortable.push([key, data[key]])
			}

			sortable.sort(function (a, b)
			{
				return b[1].id - a[1].id;
			});

			var sortedObject = [];
			for (var key in sortable) {
				sortedObject.push(sortable[key][1]);
			}

			return sortedObject;
		},

		/*****************************************
		 ************* Debug Helpers *************
		 *****************************************/

		getNumCalls: function ()
		{
			return this.calls;
		},

		getNumPostCalls: function ()
		{
			return this.postCalls;
		},

		getNumGetCalls: function ()
		{
			return this.calls - this.postCalls;
		}

	}
})(jQuery, window);
