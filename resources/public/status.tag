<status>
    <div>Siste brygg: { new Date(data.latest.regular.created).toLocaleString() } av { data.latest.regular['slack-user'] }.</div>

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
