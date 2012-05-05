module NotesHelper

  def note_form(item, opts = {})
    if opts[:photo]
      url = add_note_album_photo_path(opts[:album], opts[:photo])
    elsif opts[:dashboard]
      url = add_note_group_dashboard_path(opts[:group], opts[:dashboard])
    end

    render :partial => 'notes/shared/form',
           :locals => { :note => item, :url => url }
  end
end