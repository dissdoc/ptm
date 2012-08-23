class PhotosController < ApplicationController
  before_filter :authenticate_user!, :except => [:show, :original]
  before_filter :set_album, :only => [:new, :create]
  before_filter :album_admin?, :only => [:new, :create]
  before_filter :set_photo, :except => [:new, :create, :index, :uploads, :uploaded]
  before_filter :photo_admin?, :only => [:destroy, :update, :apply_recommend, :destroy_recommend,
    :apply_recommend_at, :destroy_recommend_at, :add_picture_name, :add_story, :edit_geo, :destroy_start_date,
    :destroy_end_date]
  before_filter :not_admin_photo?, :only => [:create_recommend, :create_recommend_at]
  before_filter :can_delete?, :only => [:deletearea]
  before_filter :set_group, :only => [:agree_link_photo, :cancel_link_photo]
  before_filter :can_delete_comment?, :only => [:destroy_note]

  def index
    @photos = current_user.photos
  end

  def new
    @photo = @album.photos.new
    @photo.build_geo
    @photo.build_share_photo
  end

  def create
    @photo = @album.photos.new(params[:photo])
    @photo.user_id = current_user.id
    if @photo.save!
      redirect_to @album
    else
      redirect_to root_path
    end
  end

  def show
    @notes = @photo.notes.all
    @note = @photo.notes.new
    if @photo.album.present?
      @album = @photo.album
    end

    if @photo.review.present?
      rv = @photo.review
      rv += 1
      @photo.update_attribute(:review, rv)
    else
      @photo.update_attribute(:review, 1)
    end

    respond_to do |format|
      format.html { render :layout => 'application_empty' }
    end
  end

  def update
    if @photo.update_attributes(params[:photo])
      redirect_to @photo
    else
      render :action => "edit"
    end
  end

  def destroy
    @photo.destroy
    redirect_to photos_path
  end

  def add_note
    current_notes = @photo.notes.all.uniq{ |note| note.user_id }

    @note = @photo.notes.new(params[:note])
    @note.user_id = current_user.id
    if @note.save!
      UserMailer.commented_photo(current_user, @photo).deliver
      UserMailer.also_added_comment(current_user, @photo, current_notes).deliver if current_notes.count > 0
    end

    respond_to do |format|
      format.js { render :template => 'notes/add_note.js.erb' }
    end
  end

  def destroy_note
    @note.destroy
    
    respond_to do |format|
      format.js { render :template => 'notes/destroy_note.js.erb' }
    end
  end

  def edit_geo
    if params[:geofield].present?
      if @photo.geo.present?
        @photo.geo.update_attribute(:address, params[:geofield])
      else
        geo = @photo.build_geo
        geo.address = params[:geofield]
        geo.save!
      end
    end

    @geo = @photo.geo
    respond_to do |format|
      format.js
    end
  end

  def create_recommend
    @recommend = @photo.recommend_geos.new

    @recommend.address = params[:geofield]
    @recommend.comment = params[:comment]
    @recommend.user = current_user
    if @recommend.save!
      UserMailer.suggested_geotag(current_user, @photo).deliver
      @message = Message.new
      @message.theme = "Recommended geotag..."
      @message.description = "User #{current_user.full_name} recommended new geolocation for photo #{@photo}"
      @message.to_user = @photo.user
      @message.from_user = current_user
      @message.save!
    end

    respond_to do |format|
      format.js
    end
  end

  def apply_recommend
    recommend = RecommendGeo.find(params[:recommend_id])

    if @photo.geo.present?
      @photo.geo.update_attributes(:address => recommend.address, :latitude => recommend.latitude,
                                    :longitude => recommend.longitude)
    else
      @geo = @photo.build_geo(:address => recommend.address, :latitude => recommend.latitude,
                       :longitude => recommend.longitude)
      @geo.save!
    end
    recommend.destroy

    respond_to do |format|
      format.js
    end
  end

  def destroy_recommend
    recommend = RecommendGeo.find(params[:recommend_id])
    recommend.destroy

    respond_to do |format|
      format.js
    end
  end

  # Recommend generated at ---------------------------------------------------------------------------------------------
  def destroy_start_date
    @photo.update_attribute(:generate, nil)

    respond_to do |format|
      format.js { render :template => 'photos/destroy_date.js.erb' }
    end
  end

  def destroy_end_date
    @photo.update_attribute(:generate_end, nil)

    respond_to do |format|
      format.js { render :template => 'photos/destroy_date.js.erb' }
    end
  end

  def edit_recommend_at
    from_at = parse_datetime(params[:from_at])
    if from_at
      @photo.update_attribute(:generate, from_at)

      to_at = parse_datetime(params[:to_at])
      @photo.update_attribute(:generate_end, to_at) if to_at
    end

    respond_to do |format|
      format.js
    end
  end

  def create_recommend_at
    @recommend = @photo.recommend_ats.new

    from_at = parse_datetime(params[:from_at])
    if from_at
      @recommend.from_at = from_at

      to_at = parse_datetime(params[:to_at])
      if to_at
        @recommend.to_at = to_at
      end

      @recommend.user = current_user
      if @recommend.save!
        UserMailer.suggested_generate(current_user, @photo).deliver
        @message = Message.new
        @message.theme = "Recommended time tag..."
        @message.description = "User #{current_user.full_name} recommended new generate date for photo #{@photo}"
        @message.to_user = @photo.user
        @message.from_user = current_user
        @message.save!
      end
    end

    respond_to do |format|
      format.js
    end
  end

  def apply_recommend_at
    recommend = RecommendAt.find(params[:recommend_id])
    @photo.update_attributes(:generate => recommend.from_at, :generate_end => recommend.to_at)
    recommend.destroy

    respond_to do |format|
      format.js
    end
  end

  def destroy_recommend_at
    recommend = RecommendAt.find(params[:recommend_id])
    recommend.destroy

    respond_to do |format|
      format.js
    end
  end
  # end of recommend generated at --------------------------------------------------------------------------------------
  def addarea
    if params[:description].present?
      @note = @photo.areatags.create!(:x => params[:x1], :y => params[:y1], :height => params[:height], :width => params[:width],
        :description => params[:description], :user_id => current_user.id)
      UserMailer.added_note(current_user, @note, @photo).deliver
    end

    respond_to do |format|
      format.js { render :template => 'areatags/addarea.js.erb' }
    end
  end

  def deletearea
    @area_id = params[:area]
    area = @photo.areatags.find(@area_id)
    area.destroy

    respond_to do |format|
      format.js { render :template => 'areatags/deletearea.js.erb' }
    end
  end

  def agree_link_photo
    @group.photo_group_joins.where(:photo_id => @photo.id).first.accept!
    redirect_to @photo
  end

  def cancel_link_photo
    @group.photo_group_joins.where(:photo_id => @photo.id).first.destroy
    redirect_to @photo
  end

  def uploads
    @object_new = current_user.photos.new
  end

  def uploaded
    #items.each do |src|
    #  @photo = Photo.create!(:image => File.new(src, "r"), :user_id => current_user.id)
    #end
    #redirect_to @root_path

    #@photo = current_user.photos.new(params[:photo])
    #if @photo.save!
    #  redirect_to @photo
    #else
    #  redirect_to root_path
    #end
    #puts params[:file].original_filename
    #params[:photo][:file].each do |photo|
      #photo_pc = Photo.create!(:image => File.new(photo.tempfile), :user_id => current_user.id)
      #puts photo.original_filename
    #end
    #render :text => params[:photo][:file].first.original_filename
    puts params[:mypic]

    render :text => 'good'
  end

  def add_picture_name
    @photo.update_attribute(:name, params[:picture_name])

    respond_to do |format|
      format.js
    end
  end

  def add_story
    @photo.update_attribute(:comment, params[:picture_story])

    respond_to do |format|
      format.js
    end
  end

  def original

  end

  protected
    def set_photo
      @photo = Photo.find(params[:id])
    end

    def set_album
      @album = Album.find(params[:album_id])
    end

    def album_admin?
      redirect_to root_path if current_user.id != @album.user.id
    end

    def photo_admin?
      redirect_to root_path if current_user.id != @photo.user.id
    end

    def not_admin_photo?
      redirect_to @photo if current_user.id == @photo.user.id
    end

    def can_delete?
      @areatag = Areatag.find(params[:area])

      !current_user.admin_of_photo?(@photo) && !current_user.admin_of_areatag?(@photo, @areatag)
    end

    def set_group
      @group = Group.find(params[:group_id])
    end

    def can_delete_comment?
      @note = @photo.notes.find(params[:note_id])
      current_user.admin_of_photo?(@photo) || current_user.admin_of_note?(@note)
    end

  private

  def parse_datetime(params)
    if params["(1i)"].blank?
      return nil
    end

    year = params["(1i)"].to_i

    if params["(2i)"].to_s.blank?
      month = 1
    else
      month = params["(2i)"].to_i
    end

    if params["(3i)"].to_s.blank?
      day = 1
    else
      day = params["(3i)"].to_i
    end

    hour   = 0
    minute = 0
    second = 0

    return DateTime.civil(year, month, day, hour, minute, second)
  end
end
