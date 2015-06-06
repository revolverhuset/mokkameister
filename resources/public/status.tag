<status>
    <div>
        <p>Siste brygg:</p>
        <p>{ new Date(data.latest.regular.created).toLocaleString() }</p>
        <p>Av:</p>
        <p>{ data.latest.regular['slack-user'] }</p>
    </div>

    this.data = {}

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
