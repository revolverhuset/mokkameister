<status>
    <div show={ data }>
        <p>Siste brygg blei laga <b>{ moment(data.latest.regular.created, moment.ISO_8601).fromNow() }</b>, av { data.latest.regular['slack-user'] }.</p>
    </div>

    this.data = null;

    load() {
        var self = this
        $.ajax({
            url: opts.url,
            dataType: 'json',
            cache: false,
            success: function(d) {
                self.data = d
                self.update()
            }})
    }

    this.load()
    setInterval(this.load, opts.interval)
</status>
