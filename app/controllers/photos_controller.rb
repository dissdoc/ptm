class PhotosController < ApplicationController
  before_filter :authenticate_user!, :except => [:show]
  before_filter :set_album
  before_filter :set_photo, :except => [:new, :create, :destroy]
  before_filter :album_admin?, :only => [:new, :create, :destroy, :edit, :update, :apply_recommend, :destroy_recommend]
  before_filter :not_admin_photo?, :only => [:recommend_geo, :create_recommend]

  def new
    @photo = @album.photos.new
    @photo.build_geo
    @photo.build_share_photo
  end

  def create
    @photo = @album.photos.new(params[:photo])
    @photo.user_id = current_user.id
    if @photo.save!
      Activity.create!(:user_id => current_user.id, :action => 'add', :object_name => 'photo', :object_link => album_photo_path(@album, @photo))
      redirect_to @album
    else
      redirect_to root_path
    end
  end

  def show
    @notes = @photo.notes.all
    @note = @photo.notes.new
  end

  def edit
    @photo.geo ? @photo.geo : @photo.build_geo
    @photo.share_photo ? @photo.share_photo : @photo.build_share_photo
  end

  def update
    if @photo.update_attributes(params[:photo])
      redirect_to [@album, @photo]
    else
      render :action => "edit"
    end
  end

  def destroy
    @photo = current_user.photos.find(params[:id])
    @album = @photo.album
    @photo.destroy
    redirect_to @album
  end

  def add_note
    @note = @photo.notes.new(params[:note])
    @note.user_id = current_user.id
    if @note.save!
      redirect_to [@album, @photo]
    else
      redirect_to root_path
    end
  end

  def recommend_geo

  end

  def create_recommend
    @recommend = @photo.geo.recommend_geos.new
    @recommend.address = params[:address]
    @recommend.comment = params[:comment]
    if @recommend.save!
      redirect_to album_photo_path(@album, @photo)
    else
      render :action => :recommend_geo
    end
  end

  def apply_recommend
    recommend = RecommendGeo.find(params[:recommend_id])
    @photo.geo.update_attributes(:address => recommend.address, :latitude => recommend.latitude,
                                    :longitude => recommend.longitude)
    recommend.destroy
    redirect_to album_photo_path(@album, @photo)
  end

  def destroy_recommend
    recommend = RecommendGeo.find(params[:recommend_id])
    recommend.destroy
    redirect_to album_photo_path(@album, @photo)
  end

  protected
    def set_photo
      @photo = @album.photos.find(params[:id])
    end

    def set_album
      @album = Album.find(params[:album_id])
    end

    def album_admin?
      redirect_to root_path if current_user.id != @album.user.id
    end

    def not_admin_photo?
      redirect_to @photo if current_user.id == @photo.user.id
    end
end