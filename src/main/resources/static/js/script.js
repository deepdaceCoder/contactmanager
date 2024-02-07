console.log('This is a random log');

const toggleSidebar=()=>{

	if($('.sidebar').is(":visible")) {
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
	} else {
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}


};

const search=()=>{
	let query=$("#search-input").val();

	if(query=='') {
		$(".search-result").hide();
	} else {

		let url=`http://localhost:8080/user/search/${query}`;

		fetch(url)
			.then((response) => {
				return response.json();
		}).then((data) => {

			let text=`<div class='list-group'>`;

			data.forEach((contact) => {
				text+=`<a href='/user/show-contacts/detail/${contact.cId}' class='list-group-item list-group-item-action'>${contact.name}</a>`;
			});

			text+=`</div>`;

			$(".search-result").html(text);
			$(".search-result").show();

		});
	}
}