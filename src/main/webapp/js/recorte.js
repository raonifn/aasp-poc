(function($) {

	Recorte = {};

	Recorte.save = function() {
		var texto = $('#texto').val();

		$.ajax({
			url : '/recorte',
			type : 'PUT',
			data : 'texto=' + encodeURIComponent(texto),
			error : function(err) {
				console.info('err:', err);
			},
			success : function(data) {
				console.info('result:', data);
				$('#newmsg').html("Created: " + data.id);
			}
		});
	};

	Recorte.search = function() {
		var query = $('#query').val();

		$.ajax({
			url : '/recorte',
			type : 'POST',
			data : 'value=' + encodeURIComponent(query),
			error : function(err) {
				console.info('err:', err);
			},
			success : function(data) {
				console.info('result:', data);

				$('#searchResult ul').html('');
				for (var i = 0; i < data.length; i++) {
					$('#searchResult ul').append(
							"<li>(" + data[i].score + ") - " + data[i].texto
									+ "</li>")
				}
			}
		});
	};

	$(document).ready(function() {
		$('#novo').click(Recorte.save);
		$('#search').click(Recorte.search);
	});

})(jQuery);