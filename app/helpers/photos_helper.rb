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

  def geolocation(item)
    if item.geo.present? && item.geo.address.present?
      address = item.geo.address
      latitude = item.geo.latitude
      longitude = item.geo.longitude
    else
      address = nil
      latitude = -34.397
      longitude = 150.644
    end

    render :partial => 'geos/shared/geolocation',
           :locals => { :address => address, :latitude => latitude,  :longitude => longitude }
  end

  def edit_geo_form(item, opts = {})
    if opts[:user] == 'admin'
      url = edit_geo_photo_path(item)
      user = true
    elsif opts[:user] == 'member'
      url = create_recommend_photo_path(item)
      user = false
    end

    render :partial => 'geos/shared/geo_form',
           :locals => { :photo => item, :url => url, :user => user }
  end
end
