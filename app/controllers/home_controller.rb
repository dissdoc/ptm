class HomeController < ApplicationController
  before_filter :authenticate_user!, :only => [:profile]

  def index
    if params[:search_tags].present?
      list_tag = params[:search_tags].split(/,\s*/)
      tags = Tag.all(:conditions => ["name IN (?)", list_tag]).collect(&:id).uniq
      ids = Tagging.all(:conditions => ["tag_id IN (?)", tags]).collect(&:photo)
      @photos = Photo.all(:conditions => ["id IN (?)", ids]).uniq
    end

    if params[:ne_lat].present?
      geos = Geo.where('latitude < ? AND latitude > ? AND longitude < ? AND longitude > ?', 
                params[:ne_lat], params[:sw_lat],
                params[:ne_lng], params[:sw_lng])
      photos_geo = geos.collect(&:photo)
      if @photos.present? && @photos.count > 0
        @photos = @photos & photos_geo
      else
        @photos = photos_geo
      end
    end

    # minD = params[:minD].to_i
    # maxD = params[:maxD].to_i
    # minD = 5 if minD < 5
    # maxD = 176 if maxD > 176

    # min_date = minD - 5 + 1826
    # max_date = maxD - 5 + 1826

    # start = DateTime.civil(min_date)
    # fin = DateTime.civil(max_date)

    # photos_date = Photo.where('generate > ? AND generate < ?', start, fin)
    # if @photos.present? && @photos.count > 0
    #   @photos = @photos & photos_date
    # else
    #   @photos = photos_date
    # end

    if @photos.blank?
      @photos = Photo.all
    end

    @photos_for_map = Photo.all

    respond_to do |format|
      format.html { render :layout => 'application_empty' }
      format.js { render :template => 'photos/refresh' }
    end
  end

  def faq
    @title_page = "FAQ"
    add_breadcrumb @title_page, ''
  end

  def contacts
    @title_page = "Contacts"
    add_breadcrumb @title_page, ''
  end

  def about
    @title_page = "About"
    add_breadcrumb @title_page, ''
  end

  def users
    if params[:query].present?
      @users = User.search(params[:query])
    else
      @users = User.free_all(current_user)
    end
  end

  def profile
    @title_page = "Profile"
    add_breadcrumb @title_page

    @invites = current_user.inviting_groups
  end

  def favorites
    @favorites = current_user.favorites
  end

  def detail
    @user = User.find(params[:id])
  end
end
