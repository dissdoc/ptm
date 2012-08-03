module PhotosHelper

  def edit_date_form(item, opts = {})
    if opts[:user] == 'admin'
      url = edit_recommend_at_photo_path(item)
    elsif opts[:user] == 'member'
      url = create_recommend_at_photo_path(item)
    end

    render :partial => 'timelines/shared/time_form',
           :locals => { :url => url, :photo => item }
  end
end
