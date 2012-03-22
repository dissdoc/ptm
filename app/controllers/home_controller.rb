class HomeController < ApplicationController
  def index
    if params[:search_locations].present?
      @locations = Geo.near(params[:search_locations])
      @photos = @locations.collect(&:photo)
    elsif params[:search_tags].present?
      @tag = Tag.get(params[:search_tags])
      @taggings = Tagging.get_by_tag(@tag)
      @photos = []
      @taggings.each do |tagging|
        @photos << tagging.photo
      end
    elsif params[:search_dt].present?
      @photos = Photo.get_by_datetime(params[:search_dt].to_s)
    else
      @photos = Photo.all
    end
  end

  def users
    if params[:query].present?
      @users = User.search(params[:query])
    else
      @users = User.all
    end
  end
end
